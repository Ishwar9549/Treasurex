package com.treasurex.login_service.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.treasurex.login_service.dto.ApiResponse;
import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterAddressRequest;
import com.treasurex.login_service.dto.RegisterPersonalRequest;
import com.treasurex.login_service.dto.RegisterSecurityQuestionsRequest;
import com.treasurex.login_service.dto.RegisterStartRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserIdCheckRequest;
import com.treasurex.login_service.dto.UserMapper;
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;
import com.treasurex.login_service.entity.SecurityQuestion;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.exception.InvalidCredentialsException;
import com.treasurex.login_service.exception.ResourceNotFoundException;
import com.treasurex.login_service.helper.Helper;
import com.treasurex.login_service.repository.UserRepository;
import com.treasurex.login_service.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final Helper helper;
	private final JwtUtil jwtUtil;

	/**
	 * Starts user registration by saving basic details and sending OTP for account
	 * verification.
	 */
	@Override
	public ApiResponse<Map<String, String>> registerStart(RegisterStartRequest request) {

		if (userRepository.findByUserId(request.getUserId()).isPresent()) {
			throw new InvalidCredentialsException("This User ID already in use: " + request.getUserId());
		}
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new InvalidCredentialsException("Password and Confirm Password do not match");
		}
		if (request.getUserId().length() < 6) {
			throw new InvalidCredentialsException("The length of the user id should not be less than 6 char");
		}
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new InvalidCredentialsException("This Email already in use: " + request.getEmail());
		}
		if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
			throw new InvalidCredentialsException("This Phone Number already in use: " + request.getPhoneNumber());
		}

		User user = userMapper.registerStartRequestToEntity(request);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		String verifyOtp = helper.generateOtp();
		user.setOtp(passwordEncoder.encode(verifyOtp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setApprovalStatus("PENDING_VERIFICATION");

		User savedUser = userRepository.save(user);

		helper.sendUserVerificationOtpToEmail(savedUser.getEmail(), verifyOtp, savedUser.getFirstName());

		Map<String, String> data = new LinkedHashMap<>();
		data.put("Next-Step", "Please verify your account using the OTP sent to your email (valid for 5 minutes).");
		data.put("Url", "http://localhost:9090/public/verify-user");

		String message = "User " + savedUser.getUserId() + " registered successfully.";

		return ApiResponse.success(data, message);
	}

	/**
	 * Saves personal details for the given user.
	 */
	@Override
	public ApiResponse<Map<String, String>> registerPersonalWithToken(String authorizationHeader,
			RegisterPersonalRequest request) {

		User user = getUserFromRegistrationTokenHeader(authorizationHeader);

		user = userMapper.registerPersonalRequestToEntity(request, user);

		userRepository.save(user);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("Next-Step", "Provide Address Details");
		data.put("Url", "http://localhost:9090/public/register/address");

		String message = "Personal information saved successfully.";

		return ApiResponse.success(data, message);
	}

	/**
	 * Saves address details for the given user.
	 */
	@Override
	public ApiResponse<Map<String, String>> registerAddressWithToken(String authorizationHeader,
			RegisterAddressRequest request) {

		User user = getUserFromRegistrationTokenHeader(authorizationHeader);

		user = userMapper.registerAddressRequestToEntity(request, user);

		userRepository.save(user);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("Next-Step", "Provide security questions Detail");
		data.put("Url", "http://localhost:9090/public/register/security_questions");

		String message = "Address information saved successfully.";

		return ApiResponse.success(data, message);

	}

	/**
	 * Saves security questions and Hash answers for the given user.
	 */
	@Override
	public ApiResponse<Void> registerSecurityQuestionsWithToken(String authorizationHeader,
			RegisterSecurityQuestionsRequest request) {

		User user = getUserFromRegistrationTokenHeader(authorizationHeader);

		user = userMapper.registerSecurityQuestionsRequestToEntity(request, user);

		userRepository.save(user);

		String message = "Security questions saved successfully. Registration completed.";

		return ApiResponse.success(null, message);
	}

	/**
	 * Verifies a newly registered user using OTP. Ensures OTP is valid and not
	 * expired and account status is updated.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyUserByOtp(UserVerifyRequest request) {

		User user = findUserByLoginId(request.getLoginId());

		if (user.isVerified()) {
			throw new InvalidCredentialsException("User is already verified.");
		}
		if (!passwordEncoder.matches(request.getOtp(), user.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		user.setVerified(true);
		user.setOtp(null);

		// generate a short-lived registration token and return it
		String registrationToken = jwtUtil.generateToken(user.getUserId());

		Map<String, String> data = new LinkedHashMap<>();
		String message;

		if ("NORMAL_USER".equals(user.getTypeOfUser())) {
			user.setApprovalStatus("VERIFIED");
			message = "Normal User " + user.getUserId() + " verified successfully.";
			data.put("security Token.", registrationToken);
			data.put("Next-Step", "Using security Token Can Access Authenticated end points");

		} else {
			user.setApprovalStatus("PENDING_APPROVAL");
			message = "BUSINESS_USER/ADVISOR_USER " + user.getUserId()
					+ " verified successfully. Please provide additional details";
			data.put("Next-Step", "Account is in pending for admin approval.");
		}
		userRepository.save(user);

		return ApiResponse.success(data, message);
	}

	/**
	 * Authenticates a user with Login ID and password, and issues JWT token if
	 * credentials are valid.
	 */
	@Override
	public ApiResponse<Map<String, String>> login(LoginRequest loginRequest) {

		User user = findUserByLoginId(loginRequest.getLoginId());

		if (!user.isVerified()) {
			throw new RuntimeException(
					"User is not verified. Please verify your account before logging in from email.");
		}

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password.");
		}

		Map<String, String> data = new LinkedHashMap<>();
		data.put("Url", "http://localhost:9090/private/test");

		if ("NORMAL_USER".equals(user.getTypeOfUser())) {

			// Normal users can login immediately after verification
			String token = jwtUtil.generateToken(user.getEmail());

			String message = "Login Successful";
			data.put("security Token.", token);
			data.put("Next-Step", "Using security Token Can Access Authenticated  end points");

			return ApiResponse.success(data, message);

		} else if ("BUSINESS_USER".equals(user.getTypeOfUser()) || "ADVISOR_USER".equals(user.getTypeOfUser())) {

			// Business/Advisor need approval
			if ("APPROVED".equals(user.getApprovalStatus())) {

				String token = jwtUtil.generateToken(user.getEmail());

				String message = "Login Successful";
				data.put("security Token.", token);
				data.put("Next-Step", "Using security Token Can Access Authenticated  end points");

				return ApiResponse.success(data, message);

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

	/**
	 * Retrieves a random security question for password recovery.
	 */
	@Override
	public ApiResponse<Map<String, String>> getSecurityQuestion(ForgotPasswordRequest forgotPasswordRequest) {

		User user = findUserByLoginId(forgotPasswordRequest.getLoginId());

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

		Map<String, String> data = new LinkedHashMap<>();
		data.put("security question.", user.getVerifyQuestion());
		data.put("Next-Step", "Verify-security-question");
		data.put("Url", "http://localhost:9090/public/verify-security-question");

		String message = "Please provide an answer to this security question.";

		return ApiResponse.success(data, message);

	}

	/**
	 * Validates security answer and sends OTP for password reset if correct.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifySecurityQuestionAndSendOtp(
			VerifySecurityAnswerRequest verifySecurityAnswerRequest) {

		User user = findUserByLoginId(verifySecurityAnswerRequest.getLoginId());

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
		String providedAnswerHash = helper.hashAnswer(verifySecurityAnswerRequest.getSecurityAnswer());
		if (!matchedQuestion.getAnswerHash().equals(providedAnswerHash)) {
			throw new RuntimeException("Incorrect security answer.");
		}

		String otp = helper.generateOtp();
		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setVerifyQuestion("");
		userRepository.save(user);

		helper.sendPasswordResetOtpEmail(user.getEmail(), otp, user.getFirstName());

		Map<String, String> data = new LinkedHashMap<>();
		data.put("Next-Step", "Reset-password using the OTP sent to your email (valid for 5 minutes).");
		data.put("Url", "http://localhost:9091/public/reset-password");

		String message = "Security question verified. OTP sent to your registered email/phone";

		return ApiResponse.success(data, message);
	}

	/**
	 * Resets password after verifying OTP.
	 */
	@Override
	public ApiResponse<Void> resetPasswordByOtp(ResetPasswordRequest resetPasswordRequest) {

		if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
			throw new InvalidCredentialsException("New Password and Confirm Password do not match");
		}
		User user = findUserByLoginId(resetPasswordRequest.getLoginId());

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}
		if (!passwordEncoder.matches(resetPasswordRequest.getOtp(), user.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}
		if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Password should not be same as Old password.");
		}

		user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
		user.setOtp("null");
		userRepository.save(user);

		String message = "PASSWORD_RESET_SUCCESS";

		return ApiResponse.success(null, message);

	}

	/**
	 * Resends OTP for either account verification or password reset.
	 */
	@Override
	public ApiResponse<Void> otpResend(ReSendOtpRequest reSendOtpRequest) {

		User user = findUserByLoginId(reSendOtpRequest.getLoginId());
		String verifyOtp = helper.generateOtp();
		user.setOtp(passwordEncoder.encode(verifyOtp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		userRepository.save(user);

		if (reSendOtpRequest.getOtpFor().equalsIgnoreCase("Account-Verification")) {
			if (user.isVerified()) {
				throw new RuntimeException("User Account is already verified.");
			}
			helper.sendUserVerificationOtpToEmail(user.getEmail(), verifyOtp, user.getFirstName());
		} else if (reSendOtpRequest.getOtpFor().equalsIgnoreCase("Password-Reset")) {
			if (!user.isVerified()) {
				throw new RuntimeException("User is not verified. Please verify your account first.");
			}
			helper.sendPasswordResetOtpEmail(user.getEmail(), verifyOtp, user.getFirstName());
		} else {
			throw new RuntimeException("otp for value should be - Account-Verification or Password-Reset");
		}

		String message = "OTP has been resent to your email address (valid for 5 minutes).";

		return ApiResponse.success(null, message);
	}

	/**
	 * Retrieves the user ID associated with a given login identifier.
	 */
	@Override
	public ApiResponse<Void> rememberUserId(RememberUserIdRequest rememberUserIdRequest) {

		User user = findUserByLoginId(rememberUserIdRequest.getLoginId());

		String message = "Your user Id is : " + user.getUserId();
		return ApiResponse.success(null, message);
	}

	/**
	 * Changes user password after validating old password.
	 */
	@Override
	public ApiResponse<Void> changePassword(ChangePasswordRequest changePasswordRequest) {

		if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
			throw new InvalidCredentialsException("New Password and Confirm Password do not match");
		}

		User user = findUserByLoginId(changePasswordRequest.getLoginId());

		if (!user.isVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}
		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Old password is incorrect.");
		}
		if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Password should not be same as Old password.");
		}
		user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
		userRepository.save(user);

		String message = "Password changed successfully.";

		return ApiResponse.success(null, message);
	}

	/**
	 * Check if userId is available or not (for front end validation)
	 */
	@Override
	public ApiResponse<Void> isUserIdAvailable(UserIdCheckRequest request) {

		if (request.getUserId().length() < 6) {
			throw new InvalidCredentialsException("The length of the userId should not be less than 6 characters");
		}

		boolean exists = userRepository.existsByUserId(request.getUserId().trim());

		if (exists) {
			return ApiResponse.error(409, "User ID is already taken. Please try a different one.");
		} else {
			return ApiResponse.success(null, "User ID is available.");
		}
	}

	/**
	 * Find user by email, phone, or userId for flexibility.
	 */
	private User findUserByLoginId(String loginId) {
		if (loginId.contains("@")) {
			return userRepository.findByEmail(loginId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with Email: " + loginId));
		} else if (loginId.matches("\\d{10}")) {
			return userRepository.findByPhoneNumber(loginId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with Phone: " + loginId));
		} else {
			return userRepository.findByUserId(loginId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with User ID: " + loginId));
		}
	}

	// inside UserServiceImp (existing class)
	private static final String BEARER_PREFIX = "Bearer ";

	private User getUserFromRegistrationTokenHeader(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			throw new InvalidCredentialsException("Missing or invalid Authorization header (expected Bearer token)");
		}
		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
		if (!jwtUtil.validateToken(token)) {
			throw new InvalidCredentialsException("Invalid or expired token");
		}
		String userId = jwtUtil.extractSubject(token);
		User user = findUserByLoginId(userId);

		return userRepository.findByUserId(user.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found for token subject: " + userId));
	}
}