package com.treasurex.login_service.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordByOtpRequest;
import com.treasurex.login_service.dto.ForgotPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordByOtpRequest;
import com.treasurex.login_service.dto.ResetPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.UserMapper;
import com.treasurex.login_service.dto.VerifyRequest;
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
	private final EmailService emailService;
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

		String verifyOtp = generateOtp(4);
		user.setOtp(verifyOtp);
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

		User savedUser = userRepository.save(user);

		sendVerifyUserMail(savedUser.getEmail(), verifyOtp, savedUser.getFirstName());

		log.info("User registered successfully with id: {}", savedUser.getId());

		return "User registered successfully with User ID: " + savedUser.getUserId()
				+ ". Please verify your account using the OTP sent to your email (valid for 5 minutes).";
	}

	@Override
	public String verifyUser(VerifyRequest verifyRequest) {

		log.info("Attempting to verify user with email: {}", verifyRequest.getEmail());

		User user = userRepository.findByEmail(verifyRequest.getEmail()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with email: " + verifyRequest.getEmail()));

		if (user.isVerified()) {
			throw new RuntimeException("User is already verified.");
		}
		if (!user.getOtp().equals(verifyRequest.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		user.setVerified(true);
		user.setOtp("null");
		userRepository.save(user);

		log.info("User verified successfully for email: {}", user.getEmail());

		return "User " + user.getEmail() + " has been verified successfully. You can now log in.";
	}

	@Override
	public String login(LoginRequest loginRequest) {

		log.info("Attempting login for email: {}", loginRequest.getUserId());

		User user = userRepository.findByUserId(loginRequest.getUserId()).orElseThrow(() -> {
			log.warn("Login failed - user not found with user Id: {}", loginRequest.getUserId());
			return new ResourceNotFoundException("User not found with User ID : " + loginRequest.getUserId());
		});

		if (!user.isVerified()) {
			throw new RuntimeException(
					"User is not verified. Please verify your account before logging in from email.");
		}
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password.");
		}

		log.info("Login successful for user ID: {}", loginRequest.getUserId());
		return jwtUtil.generateToken(user.getEmail());
	}

	@Override
	public String forgotPassword(ForgotPasswordByOtpRequest forgotPasswordByOtpRequest) {
		log.info("Forgot password request received for email: {}", forgotPasswordByOtpRequest.getEmail());

		User user = userRepository.findByEmail(forgotPasswordByOtpRequest.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException(
						"User not found with email: " + forgotPasswordByOtpRequest.getEmail()));

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account from the mail otp first.");
		}

		String otp = generateOtp(4);
		user.setOtp(otp);
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		userRepository.save(user);

		sendOtpMail(forgotPasswordByOtpRequest.getEmail(), otp, user.getFirstName());

		log.debug("Generated password reset OTP for {}: {}", forgotPasswordByOtpRequest.getEmail(), otp);

		return "OTP has been sent to your email address (valid for 5 minutes).";
	}

	@Override
	public String resetPassword(ResetPasswordByOtpRequest resetPasswordByOtpRequest) {
		log.info("Reset password request received for email: {}", resetPasswordByOtpRequest.getEmail());

		User user = userRepository.findByEmail(resetPasswordByOtpRequest.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException(
						"User not found with email: " + resetPasswordByOtpRequest.getEmail()));

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}
		if (!user.getOtp().equals(resetPasswordByOtpRequest.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		user.setPassword(passwordEncoder.encode(resetPasswordByOtpRequest.getNewPassword()));
		user.setOtp("null");
		userRepository.save(user);

		log.debug("Password updated successfully for email: {}", resetPasswordByOtpRequest.getEmail());

		return "Password updated successfully.";
	}

	@Override
	public String changePassword(ChangePasswordRequest changePasswordRequest) {
		log.info("Change password request received for email: {}", changePasswordRequest.getEmail());

		User user = userRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with email: " + changePasswordRequest.getEmail()));

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}
		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Old password is incorrect.");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
		userRepository.save(user);

		log.debug("Password changed successfully for email: {}", changePasswordRequest.getEmail());

		return "Password changed successfully.";
	}

	@Override
	public String otpResend(ReSendOtpRequest reSendOtpRequest) {
		log.info("OTP resend request received for email: {}", reSendOtpRequest.getEmail());

		User user = userRepository.findByEmail(reSendOtpRequest.getEmail()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with email: " + reSendOtpRequest.getEmail()));

		String verifyOtp = generateOtp(4);
		user.setOtp(verifyOtp);
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		userRepository.save(user);

		if (reSendOtpRequest.getOtpFor().equalsIgnoreCase("Account-Verification")) {
			if (user.isVerified()) {
				throw new RuntimeException("User Account is already verified.");
			}
			sendVerifyUserMail(user.getEmail(), verifyOtp, user.getFirstName());
		} else if (reSendOtpRequest.getOtpFor().equalsIgnoreCase("Password-Reset")) {
			sendOtpMail(user.getEmail(), verifyOtp, user.getFirstName());
		} else {
			throw new RuntimeException("otp for value should be - Account-Verification or Password-Reset");
		}

		log.debug("OTP resent to email: {}", reSendOtpRequest.getEmail());
		return "OTP has been resent to your email address (valid for 5 minutes).";
	}

//------------------------------------------------------------------------- functions ----------------
	private String generateOtp(int length) {
		StringBuilder otp = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	private void sendVerifyUserMail(String mail, String otp, String name) {
		String subject = "Account Verification - OTP Code";

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; background-color:#f9f9f9; padding:20px;'>"
				+ "<div style='max-width:600px; margin:auto; background:#fff; padding:20px; border-radius:10px; "
				+ "box-shadow:0 2px 8px rgba(0,0,0,0.1);'>" + "<h2 style='color:#333;'>Email Verification</h2>"
				+ "<p>Dear " + name + ",</p>" + "<p>Thank you for registering with us.</p>"
				+ "<p>Your One-Time Password (OTP) for account verification is: "
				+ "<b style='color:#2563eb; font-size:18px;'>" + otp + "</b></p>"
				+ "<p>This OTP is valid for <b>5 minutes</b> and will expire afterwards.</p>"
				+ "<p style='color:red;'><i>Do not share this OTP with anyone for security reasons.</i></p>"
				+ "<br><p>Regards,<br><b>Treasurex Team</b></p>" + "</div></body></html>";

		emailService.sendEmail(mail, subject, body);
	}

	private void sendOtpMail(String mail, String otp, String name) {
		String subject = "Password Reset - OTP Code";

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; background-color:#f9f9f9; padding:20px;'>"
				+ "<div style='max-width:600px; margin:auto; background:#fff; padding:20px; border-radius:10px; "
				+ "box-shadow:0 2px 8px rgba(0,0,0,0.1);'>" + "<h2 style='color:#333;'>Password Reset Request</h2>"
				+ "<p>Dear " + name + ",</p>" + "<p>Your One-Time Password (OTP) for password reset is: "
				+ "<b style='color:#2563eb; font-size:18px;'>" + otp + "</b></p>"
				+ "<p>This OTP is valid for <b>5 minutes</b> and will expire afterwards.</p>"
				+ "<p>Please use this OTP to reset your account password.</p>"
				+ "<p style='color:red;'><i>Do not share this OTP with anyone for security reasons.</i></p>"
				+ "<br><p>Regards,<br><b>Treasurex Team</b></p>" + "</div></body></html>";

		emailService.sendEmail(mail, subject, body);
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

//======================================================  changes  ================================================
	@Override
	public String rememberUserId(RememberUserIdRequest rememberUserIdRequest) {
		log.info("RememberUserId: Request received for email: {}", rememberUserIdRequest.getEmail());

		User user = userRepository.findByEmail(rememberUserIdRequest.getEmail()).orElseThrow(() -> {
			log.error("RememberUserId: User not found with email: {}", rememberUserIdRequest.getEmail());
			return new ResourceNotFoundException("User not found with email: " + rememberUserIdRequest.getEmail());
		});

		log.info("RememberUserId: Successfully retrieved UserId={} for email={}", user.getUserId(),
				rememberUserIdRequest.getEmail());
		return  user.getUserId();
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
			log.warn("ResetPasswordBySecurityQuestion: Incorrect answer for question [{}] for User Id={}", askedQuestion,
					resetPasswordBySecurityQuestionRequest.getUserId());
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
}