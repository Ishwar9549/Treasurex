package com.treasurex.userservice.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.treasurex.userservice.dto.AdvisorDetailsRequest;
import com.treasurex.userservice.dto.ApiResponse;
import com.treasurex.userservice.dto.BusinessDetailsRequest;
import com.treasurex.userservice.dto.ChangeMpinRequest;
import com.treasurex.userservice.dto.ChangePasswordRequest;
import com.treasurex.userservice.dto.EmailRequest;
import com.treasurex.userservice.dto.LoginRequest;
import com.treasurex.userservice.dto.MpinRequest;
import com.treasurex.userservice.dto.OtpPurpose;
import com.treasurex.userservice.dto.OtpVerifyRequest;
import com.treasurex.userservice.dto.PasswordRequest;
import com.treasurex.userservice.dto.PhoneNumberRequest;
import com.treasurex.userservice.dto.ReSendOtpRequest;
import com.treasurex.userservice.dto.RegisterEmailRequest;
import com.treasurex.userservice.dto.RegisterPhoneNumberRequest;
import com.treasurex.userservice.dto.RememberUserNameRequest;
import com.treasurex.userservice.dto.UserDetailsRequest;
import com.treasurex.userservice.dto.UserNameCheckRequest;
import com.treasurex.userservice.exception.EmailAlreadyInUseException;
import com.treasurex.userservice.exception.EmailConflictException;
import com.treasurex.userservice.exception.EmailNotVerifiedException;
import com.treasurex.userservice.exception.InvalidCredentialsException;
import com.treasurex.userservice.exception.InvalidMpinException;
import com.treasurex.userservice.exception.InvalidOtpPurposeException;
import com.treasurex.userservice.exception.InvalidTokenPurposeException;
import com.treasurex.userservice.exception.LoginIdValidationException;
import com.treasurex.userservice.exception.MpinMismatchException;
import com.treasurex.userservice.exception.MpinValidationException;
import com.treasurex.userservice.exception.NotFoundException;
import com.treasurex.userservice.exception.OtpExpiredException;
import com.treasurex.userservice.exception.OtpMismatchException;
import com.treasurex.userservice.exception.PasswordMismatchException;
import com.treasurex.userservice.exception.PasswordValidationException;
import com.treasurex.userservice.exception.PhoneAlreadyRegisteredException;
import com.treasurex.userservice.exception.PhoneMismatchException;
import com.treasurex.userservice.exception.PhoneNotVerifiedException;
import com.treasurex.userservice.exception.ReferralCodeValidationException;
import com.treasurex.userservice.exception.UserIdAlreadyTakenException;
import com.treasurex.userservice.exception.UserNameAlreadyTakenException;
import com.treasurex.userservice.exception.UserTypeNotAllowedException;
import com.treasurex.userservice.helper.Helper;
import com.treasurex.userservice.helper.UserConverter;
import com.treasurex.userservice.mapper.AdvisorDetailsMapper;
import com.treasurex.userservice.mapper.BusinessDetailsMapper;
import com.treasurex.userservice.mapper.ReferalSystemMapper;
import com.treasurex.userservice.mapper.UserDetailsMapper;
import com.treasurex.userservice.mapper.UserMapper;
import com.treasurex.userservice.model.AdvisorDetails;
import com.treasurex.userservice.model.BusinessDetails;
import com.treasurex.userservice.model.ReferalSystem;
import com.treasurex.userservice.model.User;
import com.treasurex.userservice.model.UserDetails;
import com.treasurex.userservice.security.JwtPurpose;
import com.treasurex.userservice.security.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private static final String BEARER_PREFIX = "Bearer ";

	private final UserMapper userMapper;
	private final UserDetailsMapper userDetailsMapper;
	private final AdvisorDetailsMapper advisorDetailsMapper;
	private final BusinessDetailsMapper businessDetailsMapper;
	private final ReferalSystemMapper referalSystemMapper;

	private final UserConverter userConverter;
	private final Helper helper;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	/**
	 * Registers a user using phone number. - Validates if phone number already
	 * exists - Generates and stores OTP with expiry - Sends OTP via SMS - Returns
	 * JWT token for further verification steps
	 */
	@Override
	@Transactional
	public ApiResponse<Map<String, String>> registerPhoneNumber(RegisterPhoneNumberRequest request) {

		log.info("Starting phone registration for phoneNumber={}", request.getPhoneNumber());

		// Check if phone number already exists
		if (userMapper.findByPhoneNumber(request.getPhoneNumber()) != null) {
			log.warn("Phone number already registered: {}", request.getPhoneNumber());
			throw new PhoneAlreadyRegisteredException("Phone number already registered.");
		}

		// Map request to User entity
		User user = userConverter.registerPhoneNumberRequestToEntity(request);
		log.debug("User entity mapped for phoneNumber={}", request.getPhoneNumber());

		// Generate and set OTP
		String otp = generateAndSetOtp(user, OtpPurpose.REGISTER_PHONE, 5);

		// Save user
		userMapper.save(user);
		log.info("User saved successfully with id={} and phoneNumber={}", user.getId(), user.getPhoneNumber());

		// Send OTP to phone
		helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
		log.info("OTP sent successfully to phoneNumber={}", user.getPhoneNumber());

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_PHONE, null);
		log.debug("JWT token generated for phoneNumber={}", user.getPhoneNumber());

		// Prepare response
		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "Phone Number " + user.getPhoneNumber() + " registered successfully.";
		log.info("Phone registration completed successfully for phoneNumber={}", user.getPhoneNumber());

		return ApiResponse.created(data, message);
	}

	/**
	 * Verifies phone number using OTP. Requires a valid registration JWT in
	 * Authorization header.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyPhoneNumber(String authorizationHeader, OtpVerifyRequest request) {

		log.info("Phone OTP verification request received");

		// Extract purpose from JWT // Get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.REGISTER_VERIFY_PHONE);

		// validate OTP
		validateOtp(user, request.getOtp());

		// Mark phone as verified and clear OTP
		user.setPhoneVerified(true);

		// clean OTP
		cleanUpOTP(user);

		userMapper.update(user);

		// Generate next JWT token based on purpose
		JwtPurpose nextPurpose = user.getOtpPurpose().toNextPhoneJwtPurpose();

		String token = jwtUtil.generateToken(user.getPhoneNumber(), nextPurpose, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("Phone number verified successfully for phone={}", user.getPhoneNumber());

		return ApiResponse.success(data, "OTP verified successfully.");
	}

	/**
	 * Create and set password for verified user
	 */
	@Override
	public ApiResponse<Map<String, String>> createPassword(String authorizationHeader, PasswordRequest request) {

		log.info("Create password service called");

		// Extract user from JWT
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.CREATE_PASSWORD);

		// Ensure phone is verified before allowing password set
		requirePhoneVerified(user);

		// Validate Password
		validatePassword(request);

		// Encode and set password
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		// Persist changes
		userMapper.update(user);

		// Generate JWT token for next step (email registration)
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_EMAIL, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("Password created successfully for phone={}", user.getPhoneNumber());

		return ApiResponse.success(data, "Password set successfully.");
	}

	/**
	 * Check if username is available or not (for frontend validation)
	 */
	@Override
	public ApiResponse<Void> checkUsernameAvailability(UserNameCheckRequest request) {

		String userName = request.getUserName().trim();

		log.info("Checking username availability for username={}", userName);

		boolean exists = userMapper.existsByUserName(userName);

		if (exists) {
			log.info("Username already taken: {}", userName);
			throw new UserIdAlreadyTakenException("User ID is already taken. Please try a different one.");
		}

		log.info("Username is available: {}", userName);
		return ApiResponse.success(null, "User ID is available.");
	}

	/*
	 * Set email and send OTP to email
	 */
	@Override
	public ApiResponse<Map<String, String>> registerEmail(String authorizationHeader, RegisterEmailRequest request) {

		log.info("Register email request received for email={}", request.getEmail());

		// Check if email already exists and is verified
		User existingUser = userMapper.findByEmail(request.getEmail());

		if (existingUser != null && existingUser.isEmailVerified()) {
			log.warn("Attempt to register already verified email={}", request.getEmail());
			throw new EmailAlreadyInUseException("This email is already in use: " + request.getEmail());
		}

		// Get user from token for this operation
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.REGISTER_EMAIL);

		// Ensure phone is verified before setting email
		requirePhoneVerified(user);

		// Set email
		user.setEmail(request.getEmail());

		// Generate OTP
		String otp = generateAndSetOtp(user, OtpPurpose.REGISTER_EMAIL, 5);

		// Send OTP to email
		helper.sendOtpForEmailVerification(request.getEmail(), otp, "USER", "Email Verification Code");

		// Persist changes
		userMapper.update(user);
		log.info("User updated with email={}", request.getEmail());

		// Generate JWT token for next step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_EMAIL, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "OTP sent to email successfully.";
		log.info("OTP sent successfully to email={}", request.getEmail());

		return ApiResponse.success(data, message);
	}

	/*
	 * Verify email through OTP
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyEmail(String authorizationHeader, OtpVerifyRequest request) {

		log.info("Verify email OTP request received for authorization header={}", authorizationHeader);

		User user;
		try {
			user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.REGISTER_VERIFY_EMAIL);
		} catch (Exception e) {
			user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL);
		}

		// Ensure email is registered
		if (user.getEmail() == null) {
			log.warn("Email verification attempted but email not registered for user={}", user.getPhoneNumber());
			throw new NotFoundException("Email is not registered. Please register your email.");
		}

		// Ensure phone is verified before email verification
		requirePhoneVerified(user);

		validateOtp(user, request.getOtp());

		// Mark email as verified
		user.setEmailVerified(true);

		// clean up otp
		cleanUpOTP(user);

		userMapper.update(user);

		// Generate next JWT token based on purpose
		JwtPurpose nextPurpose = user.getOtpPurpose().toNextPhoneJwtPurpose();

		String token = jwtUtil.generateToken(user.getPhoneNumber(), nextPurpose, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "OTP verified successfully.";

		return ApiResponse.success(data, message);
	}

	/*
	 * Set details for NORMAL_USER
	 */
	@Override
	public ApiResponse<Map<String, String>> setUserDetails(String authorizationHeader, UserDetailsRequest request) {

		log.info("SetUserDetails request received for authorizationHeader={}", authorizationHeader);

		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_DETAILS);

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Ensure email is verified
		requireEmailVerified(user);

		// validate user name and email
		validateEmailAndUsername(user, request.getEmail(), request.getUserName());

		// Ensure role is NORMAL_USER
		if (!"NORMAL_USER".equals(user.getTypeOfUser())) {
			log.warn("User role mismatch for user={}: role={}", user.getPhoneNumber(), user.getTypeOfUser());
			throw new UserTypeNotAllowedException(
					"Only NORMAL_USER is allowed for this operation. You are: " + user.getTypeOfUser());
		}

		// Map details and persist
		UserDetails userDetails = userConverter.setUserDetailsRequestToEntity(user, request);

		user.setUserName(request.getUserName());
		referSystem(request.getReferralCode(), user);

		userDetailsMapper.save(userDetails);
		userMapper.update(user);

		// Generate token for next step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("SetUserDetails completed successfully for user={}", user.getPhoneNumber());

		return ApiResponse.success(data, "User details set successfully.");
	}

	/*
	 * Set details for ADVISOR_USER
	 */
	@Override
	public ApiResponse<Map<String, String>> setAdvisorDetails(String authorizationHeader,
			AdvisorDetailsRequest request) {

		log.info("SetAdvisorDetails request received for authorizationHeader={}", authorizationHeader);

		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_DETAILS);

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Ensure email is verified
		requireEmailVerified(user);

		// validate user name and email
		validateEmailAndUsername(user, request.getEmail(), request.getUserName());

		// Ensure role is ADVISOR_USER
		if (!"ADVISOR_USER".equals(user.getTypeOfUser())) {
			log.warn("User role mismatch for user={}: role={}", user.getPhoneNumber(), user.getTypeOfUser());
			throw new UserTypeNotAllowedException(
					"Only ADVISOR_USER is allowed for this operation. You are: " + user.getTypeOfUser());
		}

		// Map details and persist
		AdvisorDetails advisorDetails = userConverter.setAdvisorDetailsRequestToEntity(user, request);

		user.setUserName(request.getUserName());
		referSystem(request.getReferralCode(), user);

		advisorDetailsMapper.save(advisorDetails);
		userMapper.update(user);

		// Generate token for next step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("SetAdvisorDetails completed successfully for user={}", user.getPhoneNumber());

		return ApiResponse.success(data, "Advisor details set successfully.");
	}

	/*
	 * Set details for BUSINESS_USER
	 */
	@Override
	public ApiResponse<Map<String, String>> setBusinessDetails(String authorizationHeader,
			BusinessDetailsRequest request) {

		log.info("SetBusinessDetails request received for authorizationHeader={}", authorizationHeader);

		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_DETAILS);

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Ensure email is verified
		requireEmailVerified(user);

		// validate user name and email
		validateEmailAndUsername(user, request.getEmail(), request.getUserName());

		// Ensure role is BUSINESS_USER
		if (!"BUSINESS_USER".equals(user.getTypeOfUser())) {
			log.warn("User role mismatch for user={}: role={}", user.getPhoneNumber(), user.getTypeOfUser());
			throw new UserTypeNotAllowedException(
					"Only BUSINESS_USER is allowed for this operation. You are: " + user.getTypeOfUser());
		}

		// Map details and persist
		BusinessDetails businessDetails = userConverter.setBusinessDetailsRequestToEntity(user, request);
		user.setUserName(request.getUserName());
		referSystem(request.getReferralCode(), user);

		businessDetailsMapper.save(businessDetails);
		userMapper.update(user);

		// Generate token for next step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("SetBusinessDetails completed successfully for user={}", user.getPhoneNumber());

		return ApiResponse.success(data, "Business details set successfully.");
	}

	/*
	 * Set MPIN for user
	 */
	@Override
	public ApiResponse<Map<String, String>> createMpin(String authorizationHeader, MpinRequest request) {

		log.info("Set MPIN request received for authorizationHeader={}", authorizationHeader);

		// Validate MPIN
		validateMpin(request);

		// Retrieve user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_MPIN);

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Ensure email is verified
		requireEmailVerified(user);

		// Save hashed MPIN
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));

		userMapper.update(user);

		log.info("MPIN set successfully for user={}", user.getPhoneNumber());

		// Generate new JWT token for accessing profile
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_PROFILE, null);

		Map<String, String> data = new LinkedHashMap<>();

		data.put("token", token);

		String message = "MPIN set successfully.";

		return ApiResponse.success(data, message);
	}

	/*
	 * Login with Email / Phone number and password (returns JWT token if valid)
	 */
	@Override
	public ApiResponse<Map<String, String>> login(LoginRequest loginRequest) {

		log.info("Login attempt received for identifier: {}", loginRequest.getLoginId());

		// Find user by email or phone
		User user = findUserByLoginId(loginRequest.getLoginId());

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Ensure email is verified
		requireEmailVerified(user);

		// validate password
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			log.warn("Invalid password attempt for user={}", loginRequest.getLoginId());
			throw new InvalidCredentialsException("Invalid password.");
		}

		// Generate JWT token for MPIN access
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("Login successful for user={}", loginRequest.getLoginId());

		return ApiResponse.success(data, "Login successful.");
	}

	/*
	 * Verify MPIN for the given user.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyMpin(String authorizationHeader, String mpin) {

		log.info("Verify MPIN request received for authorization header");

		// Retrieve user from JWT token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.ACCESS_MPIN);

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Ensure email is verified
		requireEmailVerified(user);

		// Validate MPIN
		if (!passwordEncoder.matches(mpin, user.getMpinHash())) {
			log.warn("Invalid MPIN attempt for user: {}", user.getPhoneNumber());
			throw new InvalidCredentialsException("Invalid MPIN.");
		}

		// Generate JWT token for profile access
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_PROFILE, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("MPIN verified successfully for user: {}", user.getPhoneNumber());

		return ApiResponse.success(data, "MPIN verified successfully.");
	}

	/*
	 * Get OTP for email (for forgot password or forgot MPIN)
	 */
	@Override
	public ApiResponse<Map<String, String>> getEmailOtp(EmailRequest request) {

		log.info("Get email OTP request received for email: {}", request.getEmail());

		// Fetch user by email
		User user = userMapper.findByEmail(request.getEmail());
		if (user == null) {
			log.warn("User not found with email: {}", request.getEmail());
			throw new NotFoundException("User not found with email: " + request.getEmail());
		}

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Ensure email is verified
		requireEmailVerified(user);

		String otp;

		OtpPurpose purpose = resolveOtpPurpose(request.getPurpose());

		otp = generateAndSetOtp(user, purpose, 5);

		userMapper.update(user);

		// Send OTP to email
		helper.sendOtpForEmailVerification(request.getEmail(), otp, "USER", "Email Verification Code");

		log.info("OTP sent successfully to email: {}", request.getEmail());

		// Generate JWT token for verification step
		String token = generateVerificationToken(user);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		return ApiResponse.success(data, "OTP sent to email successfully.");
	}

	/*
	 * Get OTP for phone number (for forgot password or forgot MPIN)
	 */
	@Override
	public ApiResponse<Map<String, String>> getPhoneNumberOtp(String authorizationHeader, PhoneNumberRequest request) {

		log.info("Get phone OTP request received for phoneNumber: {}", request.getPhoneNumber());

		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL);

		if (!request.getPurpose().equals(user.getOtpPurpose().toString())) {
			log.warn("OTP Purpose Conflict Error..");
			throw new InvalidOtpPurposeException("OTP purpose invalid. Expected: " + user.getOtpPurpose());
		}

		// Validate phone number from token
		if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {
			log.warn("Phone number mismatch for user: token phone={}, request phone={}", user.getPhoneNumber(),
					request.getPhoneNumber());
			throw new PhoneMismatchException("Phone number does not match the logged-in user.");
		}

		// Generate OTP
		String otp = generateAndSetOtp(user, null, 5);

		// Set OTP purpose
		if ("FORGOT_PASSWORD".equalsIgnoreCase(request.getPurpose())) {
			user.setOtpPurpose(OtpPurpose.FORGOT_PASSWORD_EMAIL);
		} else if ("FORGOT_MPIN".equalsIgnoreCase(request.getPurpose())) {
			user.setOtpPurpose(OtpPurpose.FORGOT_MPIN_EMAIL);
		} else {
			log.warn("Invalid OTP purpose received: {}", request.getPurpose());
			throw new InvalidOtpPurposeException("Invalid OTP purpose: " + request.getPurpose());
		}

		// Send OTP via SMS
		helper.sendVerificationOtpToPhone(request.getPhoneNumber(), otp);
		log.info("OTP sent successfully to phoneNumber: {}", request.getPhoneNumber());

		userMapper.update(user);

		// Generate JWT token for verification step
		String token = null;
		if (user.getOtpPurpose() == OtpPurpose.FORGOT_PASSWORD_EMAIL) {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PASSWORD_VERIFY_PHONE, null);
		} else if (user.getOtpPurpose() == OtpPurpose.FORGOT_MPIN_EMAIL) {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_MPIN_VERIFY_PHONE, null);
		}

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		return ApiResponse.success(data, "OTP sent to phone successfully.");
	}

	/*
	 * Reset Password after verifying OTP
	 */
	@Override
	public ApiResponse<Map<String, String>> resetPassword(String authorizationHeader, PasswordRequest request) {

		log.info("Reset password request received");

		// Validate new password confirmation
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			log.warn("New password and confirm password mismatch");
			throw new PasswordMismatchException("New Password and Confirm Password do not match");
		}

		// Get user from JWT token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.RESET_PASSWORD);

		// Ensure phone and email are verified
		requirePhoneVerified(user);

		requireEmailVerified(user);

		// Prevent reusing old password
		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			log.warn("New password matches old password for user: {}", user.getPhoneNumber());
			throw new PasswordValidationException("New password cannot be the same as the old password.");
		}

		// Encode and set new password
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		// Clear OTP data
		user.setOtp(null);
		user.setOtpExpiry(null);

		userMapper.update(user);

		log.info("Password reset successfully for user: {}", user.getPhoneNumber());

		return ApiResponse.success(null, "Password reset successfully.");
	}

	/*
	 * Reset MPIN after verifying OTP
	 */
	@Override
	public ApiResponse<Map<String, String>> resetMpin(String authorizationHeader, MpinRequest request) {

		log.info("Reset MPIN request received");

		// Validate new MPIN confirmation
		if (!request.getNewMpin().equals(request.getConfirmMpin())) {
			log.warn("New MPIN and confirm MPIN mismatch");
			throw new MpinMismatchException("New MPIN and Confirm MPIN do not match.");
		}

		// Get user from JWT token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.RESET_MPIN);

		// Ensure phone and email are verified
		requirePhoneVerified(user);

		requireEmailVerified(user);

		// Prevent reusing old MPIN
		if (passwordEncoder.matches(request.getNewMpin(), user.getMpinHash())) {
			log.warn("New MPIN matches old MPIN for user: {}", user.getPhoneNumber());
			throw new MpinValidationException("New MPIN cannot be the same as the old MPIN.");
		}

		// Encode and set new MPIN
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));
		userMapper.update(user);

		log.info("MPIN reset successfully for user: {}", user.getPhoneNumber());

		return ApiResponse.success(null, "MPIN reset successfully.");
	}

	/*
	 * Resends OTP for account verification or credential recovery.
	 */
	@Override
	public ApiResponse<Map<String, String>> reSendOtp(ReSendOtpRequest request) {

		log.info("Re-send OTP request received for loginId: {}", request.getLoginId());

		// Find user by login ID
		User user = findUserByLoginId(request.getLoginId());

		// Generate new OTP and set expiry
		String otp = helper.generateOtp();
		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

		// Parse OTP purpose safely
		OtpPurpose otpPurpose;
		try {
			otpPurpose = OtpPurpose.valueOf(request.getOtpPurpose());
		} catch (IllegalArgumentException ex) {
			log.warn("Invalid OTP purpose provided: {}", request.getOtpPurpose());
			throw new InvalidOtpPurposeException("Invalid OTP purpose: " + request.getOtpPurpose());
		}

		user.setOtpPurpose(otpPurpose);

		userMapper.update(user);

		String message;
		String token = null;

		switch (otpPurpose) {
		case REGISTER_EMAIL -> {
			// Send OTP to email
			helper.sendOtpForEmailVerification(user.getEmail(), otp, "USER", "Email Verification Code");

			message = "OTP has been resent to your registered email address. It is valid for 5 minutes.";

			// Generate JWT token for next step
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_EMAIL, null);
		}
		case REGISTER_PHONE -> {
			helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
			message = "OTP has been resent to your registered phone number. It is valid for 5 minutes.";

			// Generate JWT token for verification step
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_PHONE, null);
			log.debug("JWT token generated for phoneNumber={}", user.getPhoneNumber());
		}
		case FORGOT_PASSWORD_EMAIL -> {
			// Send OTP to email
			helper.sendOtpForEmailVerification(user.getEmail(), otp, "USER", "Email Verification Code");

			message = "OTP has been resent to your registered email address for password reset. It is valid for 5 minutes.";

			// Generate JWT token for next step
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL, null);
		}
		case FORGOT_PASSWORD_PHONE -> {
			helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
			message = "OTP has been resent to your registered phone number for password reset. It is valid for 5 minutes.";

			// Generate JWT token for next step
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PASSWORD_VERIFY_PHONE, null);
		}
		case FORGOT_MPIN_EMAIL -> {
			// Send OTP to email
			helper.sendOtpForEmailVerification(user.getEmail(), otp, "USER", "Email Verification Code");

			message = "OTP has been resent to your registered email address for MPIN reset. It is valid for 5 minutes.";

			// Generate JWT token for next step
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_MPIN_VERIFY_EMAIL, null);
		}
		case FORGOT_MPIN_PHONE -> {
			helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
			message = "OTP has been resent to your registered phone number for MPIN reset. It is valid for 5 minutes.";

			// Generate JWT token for next step
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_MPIN_VERIFY_PHONE, null);
		}
		default -> {
			log.error("Unsupported OTP purpose: {}", otpPurpose);
			throw new InvalidOtpPurposeException("Unsupported OTP purpose: " + request.getOtpPurpose());
		}
		}

		log.info("OTP resent successfully for loginId: {}, purpose: {}", request.getLoginId(), otpPurpose);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		return ApiResponse.success(data, message);
	}

	/*
	 * Changes user password after validating old password.
	 */
	@Override
	public ApiResponse<Void> changePassword(String authorizationHeader, ChangePasswordRequest changePasswordRequest) {

		log.info("Change password request received for user with token: {}", authorizationHeader);

		// Validate new password match
		if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
			log.warn("New password and confirm password mismatch");
			throw new PasswordMismatchException("New Password and Confirm Password do not match.");
		}

		// Retrieve user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.ACCESS_PROFILE);

		// Ensure phone or email is verified
		requirePhoneVerified(user);
		requireEmailVerified(user);

		// Validate old password
		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
			log.warn("Incorrect old password provided for user: {}", user.getPhoneNumber());
			throw new InvalidCredentialsException("Old password is incorrect.");
		}

		// Prevent reuse of old password
		if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
			log.warn("Attempt to reuse old password for user: {}", user.getPhoneNumber());
			throw new PasswordValidationException("Password should not be same as old password.");
		}

		// Update password
		user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
		userMapper.update(user);

		log.info("Password changed successfully for user: {}", user.getPhoneNumber());

		return ApiResponse.success(null, "Password changed successfully.");
	}

	/*
	 * Change MPIN for the given user if old MPIN is known.
	 */
	@Override
	public ApiResponse<Void> changeMpin(String authorizationHeader, ChangeMpinRequest request) {

		log.info("Change MPIN request received for user with token: {}", authorizationHeader);

		// Retrieve user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.ACCESS_PROFILE);

		// Validate MPIN lengths
		if (request.getOldMpin().length() != 4 || request.getNewMpin().length() != 4
				|| request.getConfirmMpin().length() != 4) {
			log.warn("Invalid MPIN length attempt for user: {}", user.getPhoneNumber());
			throw new MpinValidationException("MPIN should have 4 digits.");
		}

		// Validate new MPIN match
		if (!request.getNewMpin().equals(request.getConfirmMpin())) {
			log.warn("New MPIN and confirm MPIN mismatch for user: {}", user.getPhoneNumber());
			throw new MpinMismatchException("New MPIN and Confirm MPIN do not match.");
		}

		// Ensure phone or email is verified
		// Ensure phone or email is verified
		requirePhoneVerified(user);
		requireEmailVerified(user);

		// Validate old MPIN
		if (!passwordEncoder.matches(request.getOldMpin(), user.getMpinHash())) {
			log.warn("Incorrect old MPIN provided for user: {}", user.getPhoneNumber());
			throw new InvalidCredentialsException("Old MPIN is incorrect.");
		}

		// Prevent reuse of old MPIN
		if (passwordEncoder.matches(request.getNewMpin(), user.getMpinHash())) {
			log.warn("Attempt to reuse old MPIN for user: {}", user.getPhoneNumber());
			throw new MpinValidationException("MPIN should not be same as old MPIN.");
		}

		// Update MPIN
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));
		userMapper.update(user);

		log.info("MPIN changed successfully for user: {}", user.getPhoneNumber());

		return ApiResponse.success(null, "MPIN changed successfully.");
	}

	/**
	 * Retrieves the user ID associated with a given login identifier.
	 */
	@Override
	public ApiResponse<Void> rememberUserId(RememberUserNameRequest request) {

		log.info("Remember userId request received for loginId: {}", request.getLoginId());

		User user = findUserByLoginId(request.getLoginId());

		if (user.getUserName() == null) {
			log.warn("No user found for loginId: {}", request.getLoginId());
			throw new NotFoundException("No user found with the provided identifier.");
		}

		String message = "Your user ID is: " + user.getUserName();
		log.info("User ID retrieved successfully for loginId: {}", request.getLoginId());

		return ApiResponse.success(null, message);
	}

	/**
	 * Validates the Authorization header, verifies the JWT token, extracts claims,
	 * and resolves the associated User. This method handles only expected
	 * authentication failures (missing header, invalid token, wrong purpose, user
	 * not found) and therefore does not log stack traces
	 */
	private User validateTokenAndGetUser(String authorizationHeader, JwtPurpose expectedPurpose) {

		log.debug("Validating JWT and extracting user");

		// Authorization header must be present
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			log.warn("Authorization header is missing");
			throw new InvalidCredentialsException("Authorization header is missing");
		}

		// Header must start with 'Bearer '
		if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
			log.warn("Invalid Authorization header format");
			throw new InvalidCredentialsException("Invalid Authorization header");
		}

		// Extract token from header
		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

		if (token.isEmpty()) {
			log.warn("Bearer token is empty");
			throw new InvalidCredentialsException("Bearer token is empty");
		}

		// Validate JWT signature and expiry
		if (!jwtUtil.validateToken(token)) {
			log.warn("Invalid or expired JWT token");
			throw new InvalidCredentialsException("Invalid or expired token");
		}

		// Extract claims after successful validation
		Claims claims = jwtUtil.extractAllClaims(token);

		// Purpose claim must be present
		String purposeClaim = claims.get("purpose", String.class);

		if (purposeClaim == null) {
			log.warn("JWT purpose claim is missing");
			throw new InvalidTokenPurposeException("Token purpose is missing");
		}

		// Validate purpose value
		JwtPurpose actualPurpose;

		try {
			actualPurpose = JwtPurpose.valueOf(purposeClaim);
			log.debug("JWT purpose extracted successfully: {}", actualPurpose);
		} catch (IllegalArgumentException ex) {
			log.warn("Invalid JWT purpose: {}", purposeClaim);
			throw new InvalidTokenPurposeException("Invalid token purpose: ");
		}

		// validate actual and expected token purpose
		if (actualPurpose != expectedPurpose) {
			throw new InvalidTokenPurposeException("Invalid token purpose");
		}

		// Subject contains phone number
		String subject = claims.getSubject();

		// Resolve user
		User user = userMapper.findByPhoneNumber(subject);
		if (user == null) {
			log.warn("User not found with phone number: {}", subject);
			throw new NotFoundException("User not found");
		}
		log.debug("User extracted successfully: {}", user.getPhoneNumber());
		return user;
	}

	/**
	 * Finds a user by email, phone number, or username.
	 *
	 * @param loginId The identifier provided by the user (email, phone, or
	 *                username).
	 * @return User entity corresponding to the loginId.
	 * @throws InvalidCredentialsException if loginId is null or blank.
	 * @throws ResourceNotFoundException   if no user is found for the provided
	 *                                     loginId.
	 */
	private User findUserByLoginId(String loginId) {

		log.debug("Searching for user with loginId: {}", loginId);

		if (loginId == null || loginId.trim().isEmpty()) {
			log.warn("Login ID is empty");
			throw new LoginIdValidationException("Login ID cannot be empty.");
		}

		User user;

		if (loginId.contains("@")) { // Email
			user = userMapper.findByEmail(loginId);
			if (user == null) {
				log.warn("User not found with Email: {}", loginId);
				throw new NotFoundException("User not found with Email: " + loginId);
			}
		} else if (loginId.matches("\\d{10}")) { // Phone number
			user = userMapper.findByPhoneNumber(loginId);
			if (user == null) {
				log.warn("User not found with Phone: {}", loginId);
				throw new NotFoundException("User not found with Phone: " + loginId);
			}
		} else { // Username
			user = userMapper.findByUserName(loginId);
			if (user == null) {
				log.warn("User not found with User Name: {}", loginId);
				throw new NotFoundException("User not found with User Name: " + loginId);
			}
		}

		log.debug("User found successfully: {}", user.getPhoneNumber());
		return user;
	}

	/**
	 * Handles referral system logic for a new user.
	 * 
	 * @param referralCode Referral code provided by the user (or "NA" if none).
	 * @param user         Newly registered user.
	 */
	@Transactional
	private void referSystem(String referralCode, User user) {

		log.debug("Processing referral system for user: {} with referralCode: {}", user.getPhoneNumber(), referralCode);

		ReferalSystem referrer = null;

		// Validate referral code if provided
		if (referralCode != null && !referralCode.equalsIgnoreCase("NA")) {
			referrer = referalSystemMapper.findByReferralCode(referralCode);
			if (referrer == null) {
				log.warn("Invalid referral code attempted: {}", referralCode);
				throw new ReferralCodeValidationException("Referral Code is invalid. Use NA if no referral code.");
			}
		}

		// Check if user already exists in referral system
		if (referalSystemMapper.findByPhoneNumber(user.getPhoneNumber()) != null) {
			log.debug("User {} already exists in referral system, skipping creation.", user.getPhoneNumber());
			return;
		}

		// Create new referral entry for the user
		ReferalSystem newReferral = ReferalSystem.builder().phoneNumber(user.getPhoneNumber())
				.referralCode(user.getUserName())
				.referredBy((referralCode == null || referralCode.equalsIgnoreCase("NA")) ? null : referralCode)
				.referralCount(0).referralBonus(0).build();

		referalSystemMapper.save(newReferral);
		log.info("Referral entry created successfully for user: {}", user.getPhoneNumber());

		// Update referrer's stats if applicable
		if (referrer != null) {
			referalSystemMapper.updateReferralStats(referrer.getId());
			log.info("Referral stats updated for referrer: {}", referrer.getPhoneNumber());
		}
	}

	// ------------------------------------------------------------------------------------------------------
	private void requirePhoneVerified(User user) {
		if (!user.isPhoneVerified()) {
			throw new PhoneNotVerifiedException(
					"Phone number is not verified. Please verify your phone number to proceed further.");
		}
	}

	private void requireEmailVerified(User user) {
		if (user.getEmail() == null || !user.isEmailVerified()) {
			throw new EmailNotVerifiedException("Email is not verified. Please verify your to proceed further.");
		}
	}

	// --------------------------------
	private String generateAndSetOtp(User user, OtpPurpose purpose, int expiryMinutes) {

		String otp = helper.generateOtp();

		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(expiryMinutes));
		user.setOtpPurpose(purpose);

		log.debug("OTP prepared for user={}, purpose={}, expiry={} minutes", user.getPhoneNumber(), purpose,
				expiryMinutes);

		return otp; // raw OTP needed for SMS/Email
	}

	// --------------------------------
	private void validateOtp(User user, String rawOtp) {

		// Check OTP expiry first (avoid unnecessary hash comparison)
		if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			log.warn("Expired OTP attempt for phone={}", user.getPhoneNumber());
			throw new OtpExpiredException("OTP has expired. Please resend OTP and try again.");
		}

		// Validate OTP purpose
		if (user.getOtpPurpose() == null) {
			log.warn("OTP purpose missing for user={}", user.getPhoneNumber());
			throw new InvalidOtpPurposeException("OTP purpose is missing.");
		}

		// Validate OTP
		if (!passwordEncoder.matches(rawOtp, user.getOtp())) {
			log.warn("Invalid OTP attempt for phone={}", user.getPhoneNumber());
			throw new OtpMismatchException("Invalid verification code.");
		}

	}

	// Valdate PASSWORD
	public void validatePassword(PasswordRequest request) {

		// Validate password and confirm password match
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			log.warn("New password and confirm password mismatch..");
			throw new PasswordMismatchException("New password and confirm password do not match.");
		}
	}

	// Validate MPIN
	public void validateMpin(MpinRequest request) {

		// Validate MPIN length
		if (request.getNewMpin().length() != 4) {
			log.warn("Invalid MPIN length attempt");
			throw new InvalidMpinException("MPIN should have 4 digits");
		}

		// Validate MPIN confirmation
		if (!request.getNewMpin().equals(request.getConfirmMpin())) {
			log.warn("MPIN and confirm MPIN mismatch");
			throw new MpinMismatchException("MPIN and Confirm MPIN do not match");
		}

	}

	// clean up otp
	private void cleanUpOTP(User user) {
		user.setOtp(null);
		user.setOtpExpiry(null);
	}

	// Validate username and email
	private void validateEmailAndUsername(User user, String requestEmail, String requestUserName) {

		// Check email matches
		if (!user.getEmail().equals(requestEmail)) {
			log.warn("Email mismatch for user={}: tokenEmail={}, requestEmail={}", user.getPhoneNumber(),
					user.getEmail(), requestEmail);
			throw new EmailConflictException("Email conflict error.");
		}

		// Check username availability
		if (userMapper.existsByUserName(requestUserName.trim())) {
			log.warn("Username already taken: {}", requestUserName);
			throw new UserNameAlreadyTakenException("User name is not available. Please try a different one.");
		}
	}

	// OTP set function

	private OtpPurpose resolveOtpPurpose(String purpose) {
		try {
			return OtpPurpose.valueOf(purpose.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			log.warn("Invalid OTP purpose received: {}", purpose);
			throw new InvalidOtpPurposeException("Invalid OTP purpose: " + purpose);
		}
	}

	private JwtPurpose resolveJwtPurposeForVerification(OtpPurpose otpPurpose) {

		switch (otpPurpose) {

		case REGISTER_PHONE:
			return JwtPurpose.CREATE_PASSWORD;

		case FORGOT_PASSWORD_EMAIL:
			return JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL;

		case FORGOT_PASSWORD_PHONE:
			return JwtPurpose.FORGOT_PASSWORD_VERIFY_PHONE;

		case FORGOT_MPIN_EMAIL:
			return JwtPurpose.FORGOT_MPIN_VERIFY_EMAIL;

		case FORGOT_MPIN_PHONE:
			return JwtPurpose.FORGOT_MPIN_VERIFY_PHONE;

		default:
			throw new InvalidOtpPurposeException("Unsupported OTP purpose for verification: " + otpPurpose);
		}
	}

	private String generateVerificationToken(User user) {

		JwtPurpose jwtPurpose = resolveJwtPurposeForVerification(user.getOtpPurpose());

		return jwtUtil.generateToken(user.getPhoneNumber(), jwtPurpose, null);
	}

}
//END
