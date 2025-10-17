package com.treasurex.login_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterAddressRequest;
import com.treasurex.login_service.dto.RegisterPersonalRequest;
import com.treasurex.login_service.dto.RegisterSecurityQuestionsRequest;
import com.treasurex.login_service.dto.RegisterStartRequest;
import com.treasurex.login_service.dto.RegistrationVerifyResponse;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserIdCheckRequest;
import com.treasurex.login_service.dto.UserMapper;
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;
import com.treasurex.login_service.entity.Address;
import com.treasurex.login_service.entity.SecurityQuestion;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.exception.InvalidCredentialsException;
import com.treasurex.login_service.exception.ResourceNotFoundException;
import com.treasurex.login_service.featureflag.FeatureFlagService;
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
	private final FeatureFlagService featureFlagService;
	private final JwtUtil jwtUtil;

	/**
	 * Starts user registration by saving basic details and sending OTP for account
	 * verification.
	 */
	@Override
	public String registerStart(RegisterStartRequest registerStartRequest) {

		// Check flag before proceeding
		if (!featureFlagService.isFeatureEnabled("REGISTRATION_ENABLED")) {
			throw new RuntimeException("Registration feature is temporarily disabled by admin");
		}

		if (!registerStartRequest.getPassword().equals(registerStartRequest.getConfirmPassword())) {
			throw new InvalidCredentialsException("Password and Confirm Password do not match");
		}
		if (registerStartRequest.getUserId().length() < 6) {
			throw new InvalidCredentialsException("The length of the user id should not be less than 6 char");
		}
		if (userRepository.findByEmail(registerStartRequest.getEmail()).isPresent()) {
			throw new InvalidCredentialsException("This Email already in use: " + registerStartRequest.getEmail());
		}
		if (userRepository.findByPhoneNumber(registerStartRequest.getPhoneNumber()).isPresent()) {
			throw new InvalidCredentialsException(
					"This Phone Number already in use: " + registerStartRequest.getPhoneNumber());
		}
		if (userRepository.findByUserId(registerStartRequest.getUserId()).isPresent()) {
			throw new InvalidCredentialsException("This User ID already in use: " + registerStartRequest.getUserId());
		}

		User user = userMapper.registerStartRequestToEntity(registerStartRequest);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		String verifyOtp = helper.generateOtp(4);
		user.setOtp(passwordEncoder.encode(verifyOtp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setApprovalStatus("PENDING_VERIFICATION");

		User savedUser = userRepository.save(user);

		helper.sendUserVerificationOtpToEmail(savedUser.getEmail(), verifyOtp, savedUser.getFirstName());

		return "User registered successfully with User ID: " + savedUser.getUserId()
				+ ". Please verify your account using the OTP sent to your email (valid for 5 minutes).";
	}

	/**
	 * Saves personal details for the given user.
	 */
	@Override
	public String registerPersonalWithToken(String authorizationHeader, RegisterPersonalRequest request) {

		// Check flag before proceeding
		if (!featureFlagService.isFeatureEnabled("REGISTRATION_ENABLED")) {
			throw new RuntimeException("Registration feature is temporarily disabled by admin");
		}

		User user = getUserFromRegistrationTokenHeader(authorizationHeader);

		// Update only the allowed personal fields from request
		user.setFirstName(request.getFirstName());
		user.setMiddleName(request.getMiddleName());
		user.setLastName(request.getLastName());
		user.setAlternativeEmail(request.getAlternativeEmail());
		user.setAlternativePhoneNumber(request.getAlternativePhoneNumber());
		user.setDob(request.getDob());
		user.setGovtIdType(request.getGovtIdType());
		user.setGovtIdNumber(request.getGovtIdNumber());

		userRepository.save(user);
		return "Personal information saved successfully.";
	}

	/**
	 * Saves address details for the given user.
	 */
	@Override
	public String registerAddressWithToken(String authorizationHeader, RegisterAddressRequest request) {

		// Check flag before proceeding
		if (!featureFlagService.isFeatureEnabled("REGISTRATION_ENABLED")) {
			throw new RuntimeException("Registration feature is temporarily disabled by admin");
		}
		User user = getUserFromRegistrationTokenHeader(authorizationHeader);

		Address address = Address.builder().addressLine1(request.getAddressLine1())
				.addressLine2(request.getAddressLine2()).city(request.getCity()).state(request.getState())
				.district(request.getDistrict()).postalCode(request.getPostalCode())
				.addressType(request.getAddressType()).user(user).build();

		user.setAddress(address);
		userRepository.save(user);

		return "Address information saved successfully.";
	}

	/**
	 * Saves security questions and Hash answers for the given user.
	 */
	@Override
	public String registerSecurityQuestionsWithToken(String authorizationHeader,
			RegisterSecurityQuestionsRequest request) {

		// Check flag before proceeding
		if (!featureFlagService.isFeatureEnabled("REGISTRATION_ENABLED")) {
			throw new RuntimeException("Registration feature is temporarily disabled by admin");
		}
		User user = getUserFromRegistrationTokenHeader(authorizationHeader);

		List<SecurityQuestion> questions = request.getSecurityQuestions().stream().map(dto -> SecurityQuestion.builder()
				.question(dto.getQuestion()).answerHash(helper.hashAnswer(dto.getAnswer())).user(user).build())
				.toList();

		user.setSecurityQuestions(questions);
		userRepository.save(user);

		return "Security questions saved successfully. Registration completed.";
	}

	/**
	 * Verifies a newly registered user using OTP. Ensures OTP is valid and not
	 * expired and account status is updated.
	 */
	@Override
	public RegistrationVerifyResponse verifyUserByOtp(UserVerifyRequest request) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("REGISTRATION_ENABLED")) {
	        throw new RuntimeException("Registration feature is temporarily disabled by admin");
	    }

		User user = findUserByLoginId(request.getLoginId());

		if (user.isVerified()) {
			throw new RuntimeException("User is already verified.");
		}
		if (!passwordEncoder.matches(request.getOtp(), user.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		user.setVerified(true);
		user.setOtp(null);
		String message;
		if ("NORMAL_USER".equals(user.getTypeOfUser())) {
			user.setApprovalStatus("VERIFIED");
			message = "User " + user.getUserId() + " verified successfully. You can now provide personal information.";
		} else {
			user.setApprovalStatus("PENDING_APPROVAL");
			message = "User " + user.getUserId()
					+ " verified successfully. Please provide additional details; account pending admin approval.";
		}
		userRepository.save(user);

		// generate a short-lived registration token and return it
		String registrationToken = jwtUtil.generateRegistrationToken(user.getUserId());
		return new RegistrationVerifyResponse(message, registrationToken);
	}

	/**
	 * Authenticates a user with Login ID and password, and issues JWT token if
	 * credentials are valid.
	 */
	@Override
	public String login(LoginRequest loginRequest) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("LOGIN_ENABLED")) {
	        throw new RuntimeException("LOGIN feature is temporarily disabled by admin");
	    }

		User user = findUserByLoginId(loginRequest.getLoginId());

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
		if ("NORMAL_USER".equals(user.getTypeOfUser())) {
			// Normal users can login immediately after verification
			String token = jwtUtil.generateToken(user.getEmail());
			return token;
		} else if ("BUSINESS_USER".equals(user.getTypeOfUser()) || "ADVISOR_USER".equals(user.getTypeOfUser())) {
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

	/**
	 * Retrieves a random security question for password recovery.
	 */
	@Override
	public String getSecurityQuestion(ForgotPasswordRequest forgotPasswordRequest) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("FORGOT_PASSWORD_ENABLED")) {
	        throw new RuntimeException("FORGOT_PASSWORD feature is temporarily disabled by admin");
	    }

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

		return user.getVerifyQuestion();
	}

	/**
	 * Validates security answer and sends OTP for password reset if correct.
	 */
	@Override
	public String verifySecurityQuestionAndSendOtp(VerifySecurityAnswerRequest verifySecurityAnswerRequest) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("FORGOT_PASSWORD_ENABLED")) {
	        throw new RuntimeException("FORGOT_PASSWORD feature is temporarily disabled by admin");
	    }

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

		String otp = helper.generateOtp(4);
		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setVerifyQuestion("");
		userRepository.save(user);

		helper.sendPasswordResetOtpEmail(user.getEmail(), otp, user.getFirstName());

		return "Security question verified. OTP sent to your registered email/phone";
	}

	/**
	 * Resets password after verifying OTP.
	 */
	@Override
	public String resetPasswordByOtp(ResetPasswordRequest resetPasswordRequest) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("FORGOT_PASSWORD_ENABLED")) {
	        throw new RuntimeException("FORGOT_PASSWORD feature is temporarily disabled by admin");
	    }

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

		return "PASSWORD_RESET_SUCCESS";
	}

	/**
	 * Resends OTP for either account verification or password reset.
	 */
	@Override
	public String otpResend(ReSendOtpRequest reSendOtpRequest) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("OTP_RESEND_ENABLED")) {
	        throw new RuntimeException("OTP_RESEND feature is temporarily disabled by admin");
	    }

		User user = findUserByLoginId(reSendOtpRequest.getLoginId());
		String verifyOtp = helper.generateOtp(4);
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
		return "OTP has been resent to your email address (valid for 5 minutes).";
	}

	/**
	 * Retrieves the user ID associated with a given login identifier.
	 */
	@Override
	public String rememberUserId(RememberUserIdRequest rememberUserIdRequest) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("REMEMBER_USERID_ENABLED")) {
	        throw new RuntimeException("REMEMBER_USERID feature is temporarily disabled by admin");
	    }

		User user = findUserByLoginId(rememberUserIdRequest.getLoginId());

		return "Your user Id is : " + user.getUserId();
	}

	/**
	 * Changes user password after validating old password.
	 */
	@Override
	public String changePassword(ChangePasswordRequest changePasswordRequest) {
		
		// Check flag before proceeding
	    if (!featureFlagService.isFeatureEnabled("CHANGE_PASSWORD_ENABLED")) {
	        throw new RuntimeException("CHANGE_PASSWORD_ENABLED feature is temporarily disabled by admin");
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

		return "Password changed successfully.";
	}

	// userId available or no for front end
	@Override
	public boolean isUserIdAvailable(UserIdCheckRequest request) {

		if (request.getUserId().length() < 6) {
			throw new InvalidCredentialsException("The length of the user id should not be less than 6 char");
		}
		return !userRepository.existsByUserId(request.getUserId().trim().toLowerCase());
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
		String userId = jwtUtil.validateTokenAndGetSubject(token, "REGISTRATION"); // may throw runtime exception
		return userRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for token subject: " + userId));
	}

}