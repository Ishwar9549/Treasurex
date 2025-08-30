package com.treasurex.login_service.service;

import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.treasurex.login_service.dto.UserDto;
import com.treasurex.login_service.dto.UserLoginRequest;
import com.treasurex.login_service.dto.UserMapper;
import com.treasurex.login_service.dto.UserRegisterRequest;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.exception.InvalidCredentialsException;
import com.treasurex.login_service.exception.ResourceNotFoundException;
import com.treasurex.login_service.repository.UserRepository;
import com.treasurex.login_service.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private String code = "Fail";
	private final EmailService emailService;
	private final JwtUtil jwtUtil;

	@Override
	@Transactional
	public UserDto register(UserRegisterRequest userRegisterRequest) {
		log.info("Attempting to register user with email: {}", userRegisterRequest.getEmail());

		if (userRepository.findByEmail(userRegisterRequest.getEmail()).isPresent()) {
			log.warn("Registration failed - email {} already exists", userRegisterRequest.getEmail());
			throw new InvalidCredentialsException("Email already in use: " + userRegisterRequest.getEmail());
		}

		User user = userMapper.dtoToEntity(userRegisterRequest);
		log.debug("Mapped UserRegisterRequest to User entity: {}", user);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);
		log.info("User registered successfully with id: {}", savedUser.getId());

		return userMapper.entityToDto(savedUser);
	}

	@Override
	public String login(UserLoginRequest userLoginRequest) {

		log.info("Attempting login for email: {}", userLoginRequest.getEmail());

		User user = userRepository.findByEmail(userLoginRequest.getEmail()).orElseThrow(() -> {
			log.warn("Login failed - user not found with email: {}", userLoginRequest.getEmail());
			return new ResourceNotFoundException("User not found with email: " + userLoginRequest.getEmail());
		});

		if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password");
		}

		log.info("Login successful for email: {}", userLoginRequest.getEmail());
		return jwtUtil.generateToken(user.getEmail());
	}

	@Override
	public String forgotPassword(String email) {
		log.info("Forgot password service method executed for email: {}", email);

		userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

		String otp = generateRandomCode(4);
		String toMail = email;
		String subject = "OTP- for password reset";
		String body = "<html>" + "<body style='font-family: Arial, sans-serif;'>" + "<h2>Password Reset Request</h2>"
				+ "<p>Dear User,</p>" + "<p>Your One-Time Password (OTP) is: <b style='color:blue;'>" + otp + "</b></p>"
				+ "<p>Please use this OTP to reset your password.</p>"
				+ "<p><i>Do not share this OTP with anyone.</i></p>" + "<br>"
				+ "<p>Regards,<br>Treasurex</p>" + "</body>" + "</html>";
		emailService.sendEmail(toMail, subject, body);
		log.debug("Generated password reset code for {}: {}", email, otp);

		return "OTP is sent to your mail id";
	}

	@Override
	public String resetPassword(String code, String email, String newPassword) {
		log.info("Reset password service method executed for email: {}", email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

		if (this.code.equals(code)) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			this.code = "Fail";
			log.debug("Password updated successfully for email: {}", email);
		} else {
			throw new InvalidCredentialsException("Code is not correct...");
		}
		return "Password updated successfully";
	}

	private String generateRandomCode(int length) {
		StringBuilder code = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			code.append(random.nextInt(10));
		}
		this.code = code.toString();
		return code.toString();
	}
}