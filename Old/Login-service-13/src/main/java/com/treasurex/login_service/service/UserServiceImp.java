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
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserMapper;
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;
import com.treasurex.login_service.entity.SecurityQuestion;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.exception.InvalidCredentialsException;
import com.treasurex.login_service.exception.ResourceNotFoundException;
import com.treasurex.login_service.repository.UserRepository;
import com.treasurex.login_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final JwtUtil jwtUtil;

	// Registers a new user, encode password, generates OTP,and sends verification
	// mail.
	@Override
	@Transactional
	public String register(RegisterRequest registerRequest) {

		if (userRepository.findByUserId(registerRequest.getUserId()).isPresent()) {
			throw new InvalidCredentialsException("This User ID already in use: " + registerRequest.getUserId());
		}

		if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
			throw new InvalidCredentialsException("Email already in use: " + registerRequest.getEmail());
		}

		User user = userMapper.dtoToEntity(registerRequest);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		String verifyOtp = generateOtp(4);
		user.setOtp(passwordEncoder.encode(verifyOtp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setApprovalStatus("PENDING_VERIFICATION");

		User savedUser = userRepository.save(user);

		sendVerifyUserMail(savedUser.getEmail(), verifyOtp, savedUser.getFirstName());

		return "User registered successfully with User ID: " + savedUser.getUserId()
				+ ". Please verify your account using the OTP sent to your email (valid for 5 minutes).";
	}

	// Verifies a newly registered user using OTP Ensures OTP is correct and not
	// expired.
	@Override
	public String verifyUserByOtp(UserVerifyRequest userVerifyRequest) {

		User user = userRepository.findByUserId(userVerifyRequest.getUserId()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with user Id: " + userVerifyRequest.getUserId()));

		if (user.isVerified()) {
			throw new RuntimeException("User is already verified.");
		}
		if (!passwordEncoder.matches(userVerifyRequest.getOtp(), user.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		user.setVerified(true);
		user.setOtp("null");
		if (user.getRole().equals("NORMAL_USER")) {
			user.setApprovalStatus("VERIFIED");
			userRepository.save(user);
			return "Normal User " + user.getUserId() + " has been verified successfully. You can now log in.";
		} else {
			user.setApprovalStatus("PENDING_APPROVAL");
			userRepository.save(user);
			return "BUSINESS_USER | ADVISOR_USER Id " + user.getUserId()
					+ " has been verified successfully. wait for approvel and once approved you can login.";
		}
	}

	// Authenticates user with ID and password, and issues JWT token if valid.
	@Override
	public String login(LoginRequest loginRequest) {

		User user = userRepository.findByUserId(loginRequest.getUserId()).orElseThrow(() -> {
			return new ResourceNotFoundException("User not found with User ID : " + loginRequest.getUserId());
		});

		// Check OTP verification for all roles
		if (!user.isVerified()) {
			throw new RuntimeException(
					"User is not verified. Please verify your account before logging in from email.");
		}
		// Check password
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password.");
		}
		// Role-specific approval check
		if ("NORMAL_USER".equals(user.getRole())) {
			// Normal users can login immediately after verification
			String token = jwtUtil.generateToken(user.getEmail());
			return token;
		} else if ("BUSINESS_USER".equals(user.getRole()) || "ADVISOR_USER".equals(user.getRole())) {
			// Business/Advisor need approval
			if ("APPROVED".equals(user.getApprovalStatus())) {
				String token = jwtUtil.generateToken(user.getEmail());
				return token;
			} else if ("PENDING_APPROVAL".equals(user.getApprovalStatus())) {
				throw new RuntimeException("Your account is awaiting admin approval");
			} else if ("REJECTED".equals(user.getApprovalStatus())) {
				throw new RuntimeException("Your account has been rejected by admin");
			} else {
				throw new RuntimeException("Invalid account status");
			}
		} else {
			throw new RuntimeException("Invalid user role");
		}
	}

	// Returns a random security question for password recovery.
	@Override
	public String getSecurityQuestion(ForgotPasswordRequest forgotPasswordRequest) {

		User user = userRepository.findByUserId(forgotPasswordRequest.getUserId()).orElseThrow(() -> {
			return new ResourceNotFoundException("User not found with User ID: " + forgotPasswordRequest.getUserId());
		});

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}

		List<String> listOfResult = user.getSecurityQuestions().stream().map(SecurityQuestion::getQuestion).toList();

		if (listOfResult.isEmpty()) {
			throw new RuntimeException("No security questions found for this user.");
		}

		int randomIndex = new Random().nextInt(listOfResult.size());
		String result = listOfResult.get(randomIndex);

		user.setVerifyQuestion(result);
		userRepository.save(user);

		return user.getVerifyQuestion();
	}

	// Validates security answer and sends OTP if correct.
	@Override
	public String verifySecurityQuestionAndSendOtp(VerifySecurityAnswerRequest verifySecurityAnswerRequest) {

		User user = userRepository.findByUserId(verifySecurityAnswerRequest.getUserId()).orElseThrow(() -> {
			return new ResourceNotFoundException(
					"User not found with USER ID: " + verifySecurityAnswerRequest.getUserId());
		});

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}

		// Get asked question
		String askedQuestion = user.getVerifyQuestion();
		if (askedQuestion == null) {
			throw new RuntimeException("No security question was asked for this user.");
		}

		// Match question
		SecurityQuestion matchedQuestion = user.getSecurityQuestions().stream()
				.filter(q -> q.getQuestion().equals(askedQuestion)).findFirst().orElseThrow(() -> {
					return new RuntimeException("Security question not found.");
				});

		// Hash and compare answer
		String providedAnswerHash = hashAnswer(verifySecurityAnswerRequest.getSecurityAnswer());
		if (!matchedQuestion.getAnswerHash().equals(providedAnswerHash)) {
			throw new RuntimeException("Incorrect security answer.");
		}

		String otp = generateOtp(4);
		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setVerifyQuestion("");
		userRepository.save(user);

		sendOtpMail(user.getEmail(), otp, user.getFirstName());

		return "Security question verified. OTP sent to your registered email/phone";
	}

	// Resets user password after verifying OTP.
	@Override
	public String resetPasswordByOtp(ResetPasswordRequest resetPasswordRequest) {

		User user = userRepository.findByUserId(resetPasswordRequest.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"User not found with user Id: " + resetPasswordRequest.getUserId()));

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}
		if (!passwordEncoder.matches(resetPasswordRequest.getOtp(), user.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
		user.setOtp("null");
		userRepository.save(user);

		return "PASSWORD_RESET_SUCCESS";
	}

	// Resends OTP (for verification or password reset).
	@Override
	public String otpResend(ReSendOtpRequest reSendOtpRequest) {

		User user = userRepository.findByUserId(reSendOtpRequest.getUserId()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with user Id: " + reSendOtpRequest.getUserId()));

		String verifyOtp = generateOtp(4);
		user.setOtp(passwordEncoder.encode(verifyOtp));
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
		return "OTP has been resent to your email address (valid for 5 minutes).";
	}

	// Retrieves the user ID associated with a given email.
	@Override
	public String rememberUserId(RememberUserIdRequest rememberUserIdRequest) {

		User user = userRepository.findByEmail(rememberUserIdRequest.getEmail()).orElseThrow(() -> {
			return new ResourceNotFoundException("User not found with email: " + rememberUserIdRequest.getEmail());
		});
		return "Your user Id is : " + user.getUserId();
	}

	// Changes user password after validating old password.
	@Override
	public String changePassword(ChangePasswordRequest changePasswordRequest) {

		User user = userRepository.findByUserId(changePasswordRequest.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"User not found with user Id: " + changePasswordRequest.getUserId()));

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}
		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Old password is incorrect.");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
		userRepository.save(user);

		return "Password changed successfully.";
	}

//------------------------------------------------------------------------- Private Helper Methods  ------------------------------------------------

	// Generates a numeric OTP of given length.
	private String generateOtp(int length) {
		StringBuilder otp = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	// Sends OTP email for account verification.
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

	// Sends OTP email for password reset.
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

	// Hashes a security answer using SHA-256 and Base64 encoding for secure
	// comparison with stored answers.
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