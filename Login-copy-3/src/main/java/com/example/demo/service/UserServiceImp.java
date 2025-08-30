package com.example.demo.service;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserMapper;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	private String code = "Fail";

	@Override
	public UserDto register(UserDto userDto) {
		logger.info("Register service method executed.");
		try {
			User user = userMapper.dtoToEntity(userDto);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User savedUser = userRepository.save(user);
			return userMapper.entityToDto(savedUser);
		} catch (DataIntegrityViolationException e) {
			throw new RuntimeException("Data is not correct.." + e.getMessage());
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new RuntimeException("Data is not matching with object..");
		} catch (Exception e) {
			throw new RuntimeException("Exceptin while saving data..");
		}

	}

	@Override
	public UserDto login(String email, String password) {
		logger.info("Login service method executed.");
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password");
		}
		return userMapper.entityToDto(user);
	}

	@Override
	public String forgotPassword(String email) {
		logger.info("Forgot password service method executed for email: {}", email);

		userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

		String resetCode = generateRandomCode(4); // Flexible length
		logger.debug("Generated password reset code for {}: {}", email, resetCode);

		// TODO: Send code via email in future
		return resetCode;
	}

	@Override
	public String resetPassword(String code, String email, String newPassword) {
		logger.info("Reset password service method executed for email: {}", email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

		if (this.code.equals(code)) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			this.code = "Fail";
			logger.debug("Password updated successfully for email: {}", email);
		} else {
			throw new RuntimeException("Code is not correct...");
		}
		return "d";
	}

	private String generateRandomCode(int length) {
		StringBuilder code = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			code.append(random.nextInt(10)); // digits 0–9
		}
		this.code = code.toString();
		return code.toString();
	}
}
//100%