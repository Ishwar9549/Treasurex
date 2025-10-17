package com.treasurex.login_service.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.treasurex.login_service.dto.ForgotPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.UserMapper;
import com.treasurex.login_service.entity.SecurityQuestion;
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
	private final JwtUtil jwtUtil;

	@Override
	@Transactional
	public String register(RegisterRequest registerRequest) {

		log.info("Attempting to register user with email: {}", registerRequest.getUserId());

		if (userRepository.findByUserId(registerRequest.getUserId()).isPresent()) {
			log.warn("Registration failed - user Id {} already exists", registerRequest.getUserId());
			throw new InvalidCredentialsException("This User ID already in use: " + registerRequest.getUserId());
		}

		if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
			log.warn("Registration failed - email {} already exists", registerRequest.getEmail());
			throw new InvalidCredentialsException("Email already in use: " + registerRequest.getEmail());
		}

		User user = userMapper.dtoToEntity(registerRequest);
		log.debug("Mapped UserRegisterRequest to User entity: {}", user);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		User savedUser = userRepository.save(user);

		log.info("User registered successfully with id: {}", savedUser.getId());

		return "User registered successfully with User ID: " + savedUser.getUserId();
	}

	@Override
	public String login(LoginRequest loginRequest) {

		log.info("Attempting login for email: {}", loginRequest.getUserId());

		User user = userRepository.findByUserId(loginRequest.getUserId()).orElseThrow(() -> {
			log.warn("Login failed - user not found with user Id: {}", loginRequest.getUserId());
			return new ResourceNotFoundException("User not found with User ID : " + loginRequest.getUserId());
		});

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password.");
		}

		log.info("Login successful for user ID: {}", loginRequest.getUserId());
		return jwtUtil.generateToken(user.getEmail());
	}

	@Override
	public String rememberUserId(RememberUserIdRequest rememberUserIdRequest) {
		log.info("RememberUserId: Request received for email: {}", rememberUserIdRequest.getEmail());

		User user = userRepository.findByEmail(rememberUserIdRequest.getEmail()).orElseThrow(() -> {
			log.error("RememberUserId: User not found with email: {}", rememberUserIdRequest.getEmail());
			return new ResourceNotFoundException("User not found with email: " + rememberUserIdRequest.getEmail());
		});

		log.info("RememberUserId: Successfully retrieved UserId={} for email={}", user.getUserId(),
				rememberUserIdRequest.getEmail());
		return user.getUserId();
	}

	@Override
	public String forgotPasswordBySecurityQuestion(
			ForgotPasswordBySecurityQuestionRequest forgotPasswordBySecurityQuestionRequest) {
		log.info("ForgotPasswordBySecurityQuestion: Request received for USER ID: {}",
				forgotPasswordBySecurityQuestionRequest.getUserId());

		User user = userRepository.findByUserId(forgotPasswordBySecurityQuestionRequest.getUserId()).orElseThrow(() -> {
			log.error("ForgotPasswordBySecurityQuestion: User not found with User ID: {}",
					forgotPasswordBySecurityQuestionRequest.getUserId());
			return new ResourceNotFoundException(
					"User not found with User ID: " + forgotPasswordBySecurityQuestionRequest.getUserId());
		});

		List<String> listOfResult = user.getSecurityQuestions().stream().map(SecurityQuestion::getQuestion).toList();

		if (listOfResult.isEmpty()) {
			log.warn("ForgotPasswordBySecurityQuestion: No security questions found for User ID={}",
					forgotPasswordBySecurityQuestionRequest.getUserId());
			throw new RuntimeException("No security questions found for this user.");
		}

		int randomIndex = new Random().nextInt(listOfResult.size());
		String result = listOfResult.get(randomIndex);

		user.setVerifyQuestion(result);
		userRepository.save(user);

		log.info("ForgotPasswordBySecurityQuestion: Selected security question [{}] for USER ID={}", result,
				forgotPasswordBySecurityQuestionRequest.getUserId());
		return result;
	}

	@Override
	public String resetPasswordBySecurityQuestion(
			ResetPasswordBySecurityQuestionRequest resetPasswordBySecurityQuestionRequest) {
		log.info("ResetPasswordBySecurityQuestion: Request received for email={}",
				resetPasswordBySecurityQuestionRequest.getUserId());

		// 1. Find user
		User user = userRepository.findByUserId(resetPasswordBySecurityQuestionRequest.getUserId()).orElseThrow(() -> {
			log.error("ResetPasswordBySecurityQuestion: User not found with User Id={}",
					resetPasswordBySecurityQuestionRequest.getUserId());
			return new ResourceNotFoundException(
					"User not found with USER ID: " + resetPasswordBySecurityQuestionRequest.getUserId());
		});

		// 2. Get asked question
		String askedQuestion = user.getVerifyQuestion();
		if (askedQuestion == null) {
			log.warn("ResetPasswordBySecurityQuestion: No security question was asked earlier for User Id={}",
					resetPasswordBySecurityQuestionRequest.getUserId());
			throw new RuntimeException("No security question was asked for this user.");
		}

		// 3. Match question
		SecurityQuestion matchedQuestion = user.getSecurityQuestions().stream()
				.filter(q -> q.getQuestion().equals(askedQuestion)).findFirst().orElseThrow(() -> {
					log.error("ResetPasswordBySecurityQuestion: Security question [{}] not found in DB for User Id={}",
							askedQuestion, resetPasswordBySecurityQuestionRequest.getUserId());
					return new RuntimeException("Security question not found.");
				});

		// 4. Hash and compare answer
		String providedAnswerHash = hashAnswer(resetPasswordBySecurityQuestionRequest.getSecurityAnswer());
		if (!matchedQuestion.getAnswerHash().equals(providedAnswerHash)) {
			log.warn("ResetPasswordBySecurityQuestion: Incorrect answer for question [{}] for User Id={}",
					askedQuestion, resetPasswordBySecurityQuestionRequest.getUserId());
			throw new RuntimeException("Incorrect security answer.");
		}

		// 5. Update password
		user.setPassword(passwordEncoder.encode(resetPasswordBySecurityQuestionRequest.getNewPassword()));
		user.setVerifyQuestion(null); // clear verify question
		userRepository.save(user);

		log.info("ResetPasswordBySecurityQuestion: Password updated successfully for User ID={}",
				resetPasswordBySecurityQuestionRequest.getUserId());
		return "Password updated successfully using security question.";
	}

	private String generateOtp(int length) {
		StringBuilder otp = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	private String hashAnswer(String answer) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(answer.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			throw new RuntimeException("Error hashing answer", e);
		}
	}
}