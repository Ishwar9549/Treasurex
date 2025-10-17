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
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
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
	public String registerStart(RegisterStartRequest registerStartRequest) {
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
	 * Verifies a newly registered user using OTP. Ensures OTP is valid and not
	 * expired and account status is updated.
	 */
	@Override
	public String verifyUserByOtp(UserVerifyRequest userVerifyRequest) {

		User user = findUserByLoginId(userVerifyRequest.getLoginId());

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

		if (user.getTypeOfUser().equals("NORMAL_USER")) {
			user.setApprovalStatus("VERIFIED");
			userRepository.save(user);
			return "Normal User Id" + user.getUserId()
					+ " verified successfully. You can now provide personal information.";
		} else {
			user.setApprovalStatus("PENDING_APPROVAL");
			userRepository.save(user);
			return "BUSINESS_USER | ADVISOR_USER Id " + user.getUserId() + user.getUserId()
					+ " verified successfully. Please provide additional details. "
					+ "Your account will remain in PENDING_APPROVAL status until reviewed.";
		}
	}

	/**
	 * Saves personal details for the given user.
	 */
	@Override
	public String registerPersonal(RegisterPersonalRequest registerPersonalRequest) {

		User user = findUserByLoginId(registerPersonalRequest.getLoginId());

		user = userMapper.registerPersonalRequestToEntity(registerPersonalRequest, user);

		userRepository.save(user);

		return "Personal information saved successfully. You can now provide address details.";
	}

	/**
	 * Saves address details for the given user.
	 */
	@Override
	public String registerAddress(RegisterAddressRequest registerAddressRequest) {
		User user = findUserByLoginId(registerAddressRequest.getLoginId());

		user = userMapper.registerAddressRequestToEntity(registerAddressRequest, user);

		userRepository.save(user);

		return "Address information saved successfully. You can now provide security questions.";
	}

	/**
	 * Saves security questions and Hash answers for the given user.
	 */
	@Override
	public String registerSecurityQuestions(RegisterSecurityQuestionsRequest registerSecurityQuestionsRequest) {
		User user = findUserByLoginId(registerSecurityQuestionsRequest.getLoginId());

		user = userMapper.registerSecurityQuestionsRequestToEntity(registerSecurityQuestionsRequest, user);

		userRepository.save(user);

		return "Security questions saved successfully. Registration process completed.";
	}

	/**
	 * Authenticates a user with Login ID and password, and issues JWT token if
	 * credentials are valid.
	 */
	@Override
	public String login(LoginRequest loginRequest) {

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

		User user = findUserByLoginId(rememberUserIdRequest.getLoginId());

		return "Your user Id is : " + user.getUserId();
	}

	/**
	 * Changes user password after validating old password.
	 */
	@Override
	public String changePassword(ChangePasswordRequest changePasswordRequest) {

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
	// --------------------new change implements userId suggestion feature
	@Override
	public boolean isUserIdAvailable(String userId) {
	    return !userRepository.existsByUserId(userId.trim().toLowerCase());
	}

}