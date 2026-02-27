package com.treasurex.userservice.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
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
import com.treasurex.userservice.dto.OtpVerifyRequest;
import com.treasurex.userservice.dto.PasswordRequest;
import com.treasurex.userservice.dto.PhoneNumberRequest;
import com.treasurex.userservice.dto.ReSendOtpRequest;
import com.treasurex.userservice.dto.RegisterEmailRequest;
import com.treasurex.userservice.dto.RegisterPhoneNumberRequest;
import com.treasurex.userservice.dto.RememberUserNameRequest;
import com.treasurex.userservice.dto.UserDetailsRequest;
import com.treasurex.userservice.dto.UserNameCheckRequest;
import com.treasurex.userservice.dto.VerifyMpinRequest;
import com.treasurex.userservice.enums.JwtPurpose;
import com.treasurex.userservice.enums.OtpChannel;
import com.treasurex.userservice.enums.OtpPurpose;
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
import com.treasurex.userservice.mapper.ReferralSystemMapper;
import com.treasurex.userservice.mapper.UserDetailsMapper;
import com.treasurex.userservice.mapper.UserMapper;
import com.treasurex.userservice.model.AdvisorDetails;
import com.treasurex.userservice.model.BusinessDetails;
import com.treasurex.userservice.model.ReferralSystem;
import com.treasurex.userservice.model.User;
import com.treasurex.userservice.model.UserDetails;
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
	private final ReferralSystemMapper referalSystemMapper;
	private final UserConverter userConverter;
	private final Helper helper;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	/**
	 * Registers a user using a phone number. - Validates whether the phone number
	 * already exists - Generates and stores an OTP with an expiry time - Sends the
	 * OTP via SMS - Returns a JWT token for further verification steps
	 */
	@Override
	@Transactional
	public ApiResponse<Map<String, String>> registerPhone(RegisterPhoneNumberRequest request) {

		log.info("Starting phone registration for phoneNumber={}", request.getPhoneNumber());

		// Check if phone number already exists
		if (userMapper.findByPhoneNumber(request.getPhoneNumber()) != null) {
			log.warn("Phone number already registered: {}", request.getPhoneNumber());
			throw new PhoneAlreadyRegisteredException("This phone number is already registered.");
		}

		// Mapping Register Phone Number Request to User entity
		User user = userConverter.registerPhoneNumberRequestToEntity(request);
		log.debug("User entity mapped for phoneNumber={}", request.getPhoneNumber());

		// Generate and set OTP
		String otp = generateAndSetOtp(user, OtpPurpose.REGISTER, OtpChannel.PHONE, 5);

		// Save user
		userMapper.save(user);
		log.info("User saved successfully with id={} and phoneNumber={}", user.getId(), user.getPhoneNumber());

		// Send OTP to phone
		helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
		log.info("OTP sent successfully to phoneNumber={}", user.getPhoneNumber());

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_PHONE, null);
		log.debug("JWT token generated for phoneNumber={}", user.getPhoneNumber());

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "Phone number " + user.getPhoneNumber() + " registered successfully.";
		log.info("Phone registration completed successfully for phoneNumber={}", user.getPhoneNumber());

		// Return
		return ApiResponse.created(data, message);
	}

	/**
	 * Verifies the phone number using an OTP. Requires a valid registration JWT in
	 * the Authorization header.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyRegistrationPhone(String authorizationHeader,
			OtpVerifyRequest request) {

		log.info("Phone OTP verification request received");

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.REGISTER_VERIFY_PHONE);

		// Validate OTP
		validateOtp(user, request.getOtp());

		// Mark phone as verified
		user.setPhoneVerified(true);

		// clean OTP data
		cleanUpOTP(user);

		// Update user (persist changes)
		userMapper.update(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.CREATE_PASSWORD, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "OTP verified successfully.";
		log.info("Phone number verified successfully for phone={}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(data, message);
	}

	/**
	 * Creates and sets a password for a verified user.
	 */
	@Override
	public ApiResponse<Map<String, String>> createPassword(String authorizationHeader, PasswordRequest request) {

		log.info("Create password service called");

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.CREATE_PASSWORD);

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Validate password
		validatePassword(request.getNewPassword(), request.getConfirmPassword(), null);

		// Encode and set password
		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

		// Update user (persist changes)
		userMapper.update(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_EMAIL, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "Password set successfully.";
		log.info("Password created successfully for phone={}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(data, message);
	}

	/**
	 * Checks whether the username is available (for frontend validation).
	 */
	@Override
	public ApiResponse<Void> checkUsernameAvailability(UserNameCheckRequest request) {

		// Trim extra spaces from username
		String userName = request.getUserName().trim();

		log.info("Checking username availability for username={}", userName);

		// Check whether the username already exists
		boolean exists = userMapper.existsByUserName(userName);

		// Validating
		if (exists) {
			log.info("Username already taken: {}", userName);
			throw new UserIdAlreadyTakenException("User ID is already in use. Please choose a different one.");
		}

		// Response message
		String message = "User ID is available.";
		log.info("Username is available: {}", userName);

		// Return
		return ApiResponse.success(null, message);
	}

	/*
	 * Sets the email address and sends an OTP for verification.
	 */
	@Override
	public ApiResponse<Map<String, String>> registerEmail(String authorizationHeader, RegisterEmailRequest request) {

		log.info("Register email request received for email={}", request.getEmail());

		// Check if email already exists and is verified
		User existingUser = userMapper.findByEmail(request.getEmail());

		// Validate
		if (existingUser != null && existingUser.isEmailVerified()) {
			log.warn("Attempt to register already verified email={}", request.getEmail());
			throw new EmailAlreadyInUseException("This email address is already in use: " + request.getEmail());
		}

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.REGISTER_EMAIL);

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Set email
		user.setEmail(request.getEmail());

		// Generate and set OTP
		String otp = generateAndSetOtp(user, OtpPurpose.REGISTER, OtpChannel.EMAIL, 5);

		// Send OTP to email
		helper.sendVerificationsOtpToEmail(request.getEmail(), otp, "USER", "Email Verification Code");

		// Update user (persist changes)
		userMapper.update(user);
		log.info("User updated with email={}", request.getEmail());

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_EMAIL, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "OTP has been sent to the email address successfully.";
		log.info("OTP sent successfully to email={}", request.getEmail());

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Verifies the email address using an OTP.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyRegistrationEmail(String authorizationHeader,
			OtpVerifyRequest request) {

		log.info("Verify email OTP request received for authorization header={}", authorizationHeader);

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.REGISTER_VERIFY_EMAIL);

		// Ensure email is registered
		if (user.getEmail() == null) {
			log.warn("Email verification attempted but email not registered for user={}", user.getPhoneNumber());
			throw new NotFoundException("Email is not registered. Please register your email first.");
		}

		// Ensure phone is verified
		requirePhoneVerified(user);

		// Validate OTP
		validateOtp(user, request.getOtp());

		// Mark email as verified
		user.setEmailVerified(true);

		// Clean up OTP data
		cleanUpOTP(user);

		// Update user (persist changes)
		userMapper.update(user);

		// Generate token for next step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_DETAILS, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "OTP verified successfully.";

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Sets details for a NORMAL_USER.
	 */
	@Override
	public ApiResponse<Map<String, String>> setUserDetails(String authorizationHeader, UserDetailsRequest request) {

		log.info("SetUserDetails request received for authorizationHeader={}", authorizationHeader);

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_DETAILS);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Validate username and email
		validateEmailAndUsername(user, request.getEmail(), request.getUserName());

		// Ensure role is NORMAL_USER
		validateUserType(user, "NORMAL_USER");

		// Map UserDetailsRequest to UserDetails entity
		UserDetails userDetails = userConverter.setUserDetailsRequestToEntity(user, request);

		// set user name
		user.setUsername(request.getUserName());

		// Referral management
		referSystem(request.getReferralCode(), user);

		// Save user details
		userDetailsMapper.save(userDetails);

		// Update user (persist changes)
		userMapper.update(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "User details set successfully.";
		log.info("SetUserDetails completed successfully for user={}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Sets details for an ADVISOR_USER.
	 */
	@Override
	public ApiResponse<Map<String, String>> setAdvisorDetails(String authorizationHeader,
			AdvisorDetailsRequest request) {

		log.info("SetAdvisorDetails request received for authorizationHeader={}", authorizationHeader);

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_DETAILS);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Validate username and email
		validateEmailAndUsername(user, request.getEmail(), request.getUserName());

		// Ensure role is ADVISOR_USER
		validateUserType(user, "ADVISOR_USER");

		// Map AdvisorDetailsRequest to AdvisorDetails entity
		AdvisorDetails advisorDetails = userConverter.setAdvisorDetailsRequestToEntity(user, request);

		// set user name
		user.setUsername(request.getUserName());

		// Referral management
		referSystem(request.getReferralCode(), user);

		// Save advisor details
		advisorDetailsMapper.save(advisorDetails);

		// Update user (persist changes)
		userMapper.update(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "Advisor details set successfully.";
		log.info("SetAdvisorDetails completed successfully for user={}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Sets details for a BUSINESS_USER.
	 */
	@Override
	public ApiResponse<Map<String, String>> setBusinessDetails(String authorizationHeader,
			BusinessDetailsRequest request) {

		log.info("SetBusinessDetails request received for authorizationHeader={}", authorizationHeader);

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_DETAILS);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Validate username and email
		validateEmailAndUsername(user, request.getEmail(), request.getUserName());

		// Ensure role is BUSINESS_USER
		validateUserType(user, "BUSINESS_USER");

		// Map BusinessDetailsRequest to BusinessDetails entity
		BusinessDetails businessDetails = userConverter.setBusinessDetailsRequestToEntity(user, request);

		// set user name
		user.setUsername(request.getUserName());

		// Referral management
		referSystem(request.getReferralCode(), user);

		// Save business details
		businessDetailsMapper.save(businessDetails);

		// Update user (persist changes)
		userMapper.update(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "Business details set successfully.";
		log.info("SetBusinessDetails completed successfully for user={}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Sets the MPIN for the user.
	 */
	@Override
	public ApiResponse<Map<String, String>> createMpin(String authorizationHeader, MpinRequest request) {

		log.info("Set MPIN request received for authorizationHeader={}", authorizationHeader);

		// Validate MPIN
		validateMpin(request.getNewMpin(), request.getConfirmMpin(), null);

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.SET_MPIN);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Set hashed MPIN
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));

		// Update user (persist changes)
		userMapper.update(user);
		log.info("MPIN set successfully for user={}", user.getPhoneNumber());

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_PROFILE, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "MPIN set successfully.";

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Logs in using email, phone number, or username and password. Returns a JWT
	 * token if the credentials are valid.
	 */
	@Override
	public ApiResponse<Map<String, String>> login(LoginRequest loginRequest) {

		log.info("Login attempt received for identifier: {}", loginRequest.getLoginId());

		// Find user by email, phone number, or username
		User user = findUserByLoginId(loginRequest.getLoginId());

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Validate password
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
			log.warn("Invalid password attempt for user={}", loginRequest.getLoginId());
			throw new InvalidCredentialsException("Invalid password.");
		}

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_MPIN, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "Login successful.";
		log.info("Login successful for user={}", loginRequest.getLoginId());

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Verifies the MPIN for the specified user.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyMpin(String authorizationHeader, VerifyMpinRequest request) {

		log.info("Verify MPIN request received for authorization header");

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.ACCESS_MPIN);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Validate MPIN
		if (!passwordEncoder.matches(request.getMpin(), user.getMpinHash())) {
			log.warn("Invalid MPIN attempt for user: {}", user.getPhoneNumber());
			throw new InvalidCredentialsException("Invalid MPIN.");
		}

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_PROFILE, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "MPIN verified successfully.";
		log.info("MPIN verified successfully for user: {}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Sends an OTP to the email address (for forgot password or forgot MPIN).
	 */
	@Override
	public ApiResponse<Map<String, String>> sendForgotOtpToEmail(EmailRequest request) {

		log.info("Get email OTP request received for email: {}", request.getEmail());

		// Fetch user by email
		User user = userMapper.findByEmail(request.getEmail());
		if (user == null) {
			log.warn("User not found with email: {}", request.getEmail());
			throw new NotFoundException("No user found with email: " + request.getEmail());
		}

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Generate and set OTP
		String otp = generateAndSetOtp(user, resolveOtpPurpose(request.getPurpose()), OtpChannel.EMAIL, 5);

		// Update user (persist changes)
		userMapper.update(user);

		// Send OTP to email
		helper.sendVerificationsOtpToEmail(request.getEmail(), otp, "USER", "Email Verification Code");
		log.info("OTP sent successfully to email: {}", request.getEmail());

		// Resolve JWT purpose
		JwtPurpose jwtPurpose = resolveJwtPurpose(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), jwtPurpose, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "OTP has been sent to the email address successfully.";

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Verifies the OTP sent to the registered email address as part of the forgot
	 * password or forgot MPIN flow. Returns a JWT token for the next step in the
	 * reset process.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyForgotEmail(String authorizationHeader, OtpVerifyRequest request) {

		log.info("Verify email OTP request received for authorization header={}", authorizationHeader);

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUserForForgotVerifyEmail(authorizationHeader);

		// Ensure email is registered
		if (user.getEmail() == null) {
			log.warn("Email verification attempted but email not registered for user={}", user.getPhoneNumber());
			throw new NotFoundException("Email is not registered. Please register your email first.");
		}

		// Ensure phone and email is verified
		requirePhoneAndEmailVerified(user);

		// Validate OTP
		validateOtp(user, request.getOtp());

		// Next pupose
		JwtPurpose nextPurpose;

		if (user.getOtpPurpose() == OtpPurpose.FORGOT_PASSWORD) {
			nextPurpose = JwtPurpose.FORGOT_PASSWORD_SEND_PHONE;
		} else if (user.getOtpPurpose() == OtpPurpose.FORGOT_MPIN) {
			nextPurpose = JwtPurpose.FORGOT_MPIN_SEND_PHONE;
		} else {
			throw new InvalidOtpPurposeException("Invalid OTP purpose for the forgot flow.");
		}

		// Clean up OTP data
		// cleanUpOTP(user);

		// Update user ( Persist changes )
		userMapper.update(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), nextPurpose, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "OTP verified successfully.";

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Sends an OTP to the registered phone number as part of the forgot password or
	 * forgot MPIN flow. Returns a JWT token for the next step in the reset process.
	 */
	@Override
	public ApiResponse<Map<String, String>> sendForgotOtpToPhone(String authorizationHeader,
			PhoneNumberRequest request) {

		log.info("Get phone OTP request received for phoneNumber: {}", request.getPhoneNumber());

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUserForForgotPhone(authorizationHeader);

		// Validate OTP purpose
		if (!request.getPurpose().equals(user.getOtpPurpose().toString())) {
			log.warn("OTP Purpose Conflict Error..");
			throw new InvalidOtpPurposeException("Invalid OTP purpose. Expected: " + user.getOtpPurpose());
		}

		// Validate phone number from token
		if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {
			log.warn("Phone number mismatch for user: token phone={}, request phone={}", user.getPhoneNumber(),
					request.getPhoneNumber());
			throw new PhoneMismatchException("Phone number does not match the logged-in user.");
		}

		// Generate and set OTP
		String otp = generateAndSetOtp(user, resolveOtpPurpose(request.getPurpose()), OtpChannel.PHONE, 5);

		// Send OTP via SMS
		helper.sendVerificationOtpToPhone(request.getPhoneNumber(), otp);
		log.info("OTP sent successfully to phoneNumber: {}", request.getPhoneNumber());

		// Update user (persist changes)
		userMapper.update(user);

		// Resolve JWT Purpose
		JwtPurpose jwtPurpose = resolveJwtPurpose(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), jwtPurpose, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "OTP has been sent to the phone number successfully.";

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Verifies the OTP sent to the registered phone number as part of the forgot
	 * password or forgot MPIN flow. Returns a JWT token for the next step in the
	 * reset process.
	 */

	@Override
	public ApiResponse<Map<String, String>> verifyForgotPhone(String authorizationHeader, OtpVerifyRequest request) {

		log.info("Phone OTP verification request received");

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUserForForgotVerifyPhone(authorizationHeader);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Validate OTP
		validateOtp(user, request.getOtp());

		// Generate next JWT token based on purpose
		JwtPurpose nextPurpose;

		if (user.getOtpPurpose() == OtpPurpose.FORGOT_PASSWORD) {
			nextPurpose = JwtPurpose.RESET_PASSWORD;
		} else if (user.getOtpPurpose() == OtpPurpose.FORGOT_MPIN) {
			nextPurpose = JwtPurpose.RESET_MPIN;
		} else {
			throw new InvalidOtpPurposeException("Invalid OTP purpose for the forgot flow.");
		}

		// Clean up OTP
		cleanUpOTP(user);

		// Update user (persist changes)
		userMapper.update(user);

		// Generate JWT token for verification step
		String token = jwtUtil.generateToken(user.getPhoneNumber(), nextPurpose, null);

		// Build response
		Map<String, String> data = buildResponse(token);

		// Response message
		String message = "OTP verified successfully.";
		log.info("Phone number verified successfully for phone={}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(data, message);
	}

	/*
	 * Resets the password after successful OTP verification.
	 */
	@Override
	public ApiResponse<Map<String, String>> resetPassword(String authorizationHeader, PasswordRequest request) {

		log.info("Reset password request received");

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.RESET_PASSWORD);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Validate password
		validatePassword(request.getNewPassword(), request.getConfirmPassword(), user);

		// Hash and set new password
		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

		// Clear OTP data
		cleanUpOTP(user);

		// Update user (persist changes)
		userMapper.update(user);

		// Response message
		String message = "Password reset successfully.";
		log.info("Password reset successfully for user: {}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(null, message);
	}

	/*
	 * Resets the MPIN after successful OTP verification.
	 */
	@Override
	public ApiResponse<Map<String, String>> resetMpin(String authorizationHeader, MpinRequest request) {

		log.info("Reset MPIN request received");

		// Extract purpose from JWT and get user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.RESET_MPIN);

		// Validate MPIN
		validateMpin(request.getNewMpin(), request.getConfirmMpin(), user);

		// Ensure phone and email are verified
		requirePhoneAndEmailVerified(user);

		// Hash and set new MPIN
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));

		// Update user (persist changes)
		userMapper.update(user);

		// Response message
		String message = "MPIN reset successfully.";
		log.info("MPIN reset successfully for user: {}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(null, message);
	}

	/*
	 * Changes user password after validating old password.
	 */
	@Override
	public ApiResponse<Void> changePassword(String authorizationHeader, ChangePasswordRequest request) {

		log.info("Change password request received for user with token: {}", authorizationHeader);

		// Retrieve user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.ACCESS_PROFILE);

		// Validate old password
		if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
			log.warn("Incorrect old password provided for user: {}", user.getPhoneNumber());
			throw new InvalidCredentialsException("Old password is incorrect.");
		}

		// Validate new password
		validatePassword(request.getNewPassword(), request.getConfirmPassword(), user);

		// Ensure phone or email is verified
		requirePhoneAndEmailVerified(user);

		// Update password
		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
		userMapper.update(user);

		// Response message
		String message = "Password changed successfully.";
		log.info("Password changed successfully for user: {}", user.getPhoneNumber());

		// Return
		return ApiResponse.success(null, message);
	}

	/*
	 * Change MPIN for the given user if old MPIN is known.
	 */
	@Override
	public ApiResponse<Void> changeMpin(String authorizationHeader, ChangeMpinRequest request) {

		log.info("Change MPIN request received for user with token: {}", authorizationHeader);

		// Retrieve user from token
		User user = validateTokenAndGetUser(authorizationHeader, JwtPurpose.ACCESS_PROFILE);

		// Ensure phone and email is verified
		requirePhoneAndEmailVerified(user);

		// Validate old MPIN
		if (!passwordEncoder.matches(request.getOldMpin(), user.getMpinHash())) {
			log.warn("Incorrect old MPIN provided for user: {}", user.getPhoneNumber());
			throw new InvalidCredentialsException("Old MPIN is incorrect.");
		}

		// Validate new MPIN
		validateMpin(request.getNewMpin(), request.getConfirmMpin(), user);

		// Update MPIN
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));
		userMapper.update(user);

		log.info("MPIN changed successfully for user: {}", user.getPhoneNumber());

		// Response message
		String message = "MPIN changed successfully.";

		// Return
		return ApiResponse.success(null, message);
	}

	/**
	 * Retrieves the user ID associated with a given login identifier.
	 */
	@Override
	public ApiResponse<Void> rememberUserId(RememberUserNameRequest request) {

		log.info("Remember userId request received for loginId: {}", request.getLoginId());

		// Find user by email, phone number, or username
		User user = findUserByLoginId(request.getLoginId());

		// Response message
		String message = "Your user ID is: " + user.getUsername() + ".";

		log.info("User ID retrieved successfully for loginId: {}", request.getLoginId());

		// Return
		return ApiResponse.success(null, message);
	}

	/*
	 * Resends OTP for account verification or credential recovery.
	 */
	@Override
	public ApiResponse<Map<String, String>> reSendOtp(ReSendOtpRequest request) {

		log.info("Re-send OTP request received for loginId: {}", request.getLoginId());

		// Find user by email, phone number, or username
		User user = findUserByLoginId(request.getLoginId());

		// Resolve purpose from request
		OtpPurpose otpPurpose = resolveOtpPurpose(request.getOtpPurpose());

		// Resolve OTP channel from request
		OtpChannel otpChannel = resolveOtpChannel(request.getOtpChannel());

		// Generate OTP again using previous channel
		String otp = generateAndSetOtp(user, otpPurpose, otpChannel, 5);

		// Update user (persist changes)
		userMapper.update(user);

		// Send OTP based on channel
		String message = sendOtpByChannel(user, otp);

		// Generate Token
		String token = generateVerificationToken(user, otpPurpose);

		log.info("OTP resent successfully for loginId={}, purpose={}, channel={}", request.getLoginId(), otpPurpose,
				user.getOtpChannel());

		// Build response
		Map<String, String> data = buildResponse(token);

		// Return
		return ApiResponse.success(data, message);
	}

	// =========================== Helper Methods ========================

	/**
	 * Finds a user by email, phone number, or username.
	 *
	 * @param loginId the email address, phone number, or username
	 */
	private User findUserByLoginId(String loginId) {

		log.debug("Searching for user with loginId: {}", loginId);

		// Validate that loginId is not empty
		if (loginId == null || loginId.trim().isEmpty()) {
			log.warn("Login ID is empty");
			throw new LoginIdValidationException("Login ID cannot be empty.");
		}

		User user;

		// Validating is Login id is Email
		if (loginId.contains("@")) {
			user = userMapper.findByEmail(loginId);
			if (user == null) {
				log.warn("User not found with Email: {}", loginId);
				throw new NotFoundException("No user found with email: " + loginId);
			}
		} else if (loginId.matches("\\d{10}")) {
			// Validating is Login id is Phone
			user = userMapper.findByPhoneNumber(loginId);
			if (user == null) {
				log.warn("User not found with Phone: {}", loginId);
				throw new NotFoundException("No user found with phone number: " + loginId);
			}
		} else {
			// Validating is Login id is User name
			user = userMapper.findByUserName(loginId);
			if (user == null) {
				log.warn("User not found with User Name: {}", loginId);
				throw new NotFoundException("No user found with username: " + loginId);
			}
		}

		log.debug("User found successfully: {}", user.getPhoneNumber());

		// Return
		return user;
	}

	/**
	 * Handles the referral system logic for a new user.
	 */
	@Transactional
	private void referSystem(String referralCode, User user) {

		log.debug("Processing referral system for user: {} with referralCode: {}", user.getPhoneNumber(), referralCode);

		ReferralSystem referrer = null;

		// Validate referral code if provided
		if (referralCode != null && !referralCode.equalsIgnoreCase("NA")) {
			referrer = referalSystemMapper.findByReferralCode(referralCode);
			if (referrer == null) {
				log.warn("Invalid referral code attempted: {}", referralCode);
				throw new ReferralCodeValidationException(
						"Invalid referral code. Use 'NA' if you do not have a referral code.");
			}
		}

		// Check if user already exists in referral system
		if (referalSystemMapper.findByPhoneNumber(user.getPhoneNumber()) != null) {
			log.debug("User {} already exists in referral system, skipping creation.", user.getPhoneNumber());
			return;
		}

		// Create new referral entry for the user
		ReferralSystem newReferral = ReferralSystem.builder().phoneNumber(user.getPhoneNumber())
				.referralCode(user.getUsername())
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

	/*
	 * Builds the response map for the return statement.
	 */
	private Map<String, String> buildResponse(String token) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("token", token);
		return map;
	}

	/**
	 * Generates a one-time password (OTP), hashes and encodes it, and sets it on
	 * the user entity along with the expiry time, purpose, and channel.
	 */
	private String generateAndSetOtp(User user, OtpPurpose otpPurpose, OtpChannel otpChannel, int expiryMinutes) {

		// Generate a random numeric OTP
		String otp = helper.generateOtp();

		// Hash and encode the OTP before storing it for security reasons
		user.setOtp(passwordEncoder.encode(otp));

		// Set OTP expiry time
		user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(expiryMinutes));

		// Set the purpose for which the OTP is generated
		user.setOtpPurpose(otpPurpose);

		// Set the channel through which the OTP is generated
		user.setOtpChannel(otpChannel);

		// Log debug info without revealing raw OTP
		log.debug("OTP prepared for user={}, purpose={}, channel={}, expiry={} minutes", user.getPhoneNumber(),
				otpPurpose, otpChannel, expiryMinutes);

		// Return raw OTP so it can be sent via SMS/email
		return otp;
	}

	// Clears OTP-related data from the user entity
	private void cleanUpOTP(User user) {
		user.setOtp(null);
		user.setOtpExpiryTime(null);
		user.setOtpPurpose(null);
		user.setOtpChannel(null);
	}

	/*
	 * Ensures that the phone number is verified.
	 */
	private void requirePhoneVerified(User user) {
		if (!user.isPhoneVerified()) {
			throw new PhoneNotVerifiedException(
					"Phone number is not verified. Please verify your phone number to proceed.");
		}
	}

	/*
	 * Ensures that the email address is verified.
	 */
	private void requireEmailVerified(User user) {
		if (user.getEmail() == null || !user.isEmailVerified()) {
			throw new EmailNotVerifiedException("Email is not verified. Please verify your email address to proceed.");
		}
	}

	/*
	 * Ensures that both the phone number and email address are verified.
	 */
	private void requirePhoneAndEmailVerified(User user) {
		requirePhoneVerified(user);
		requireEmailVerified(user);
	}

	/*
	 * Validates the provided OTP against the stored OTP, expiry time, purpose, and
	 * channel.
	 */
	private void validateOtp(User user, String rawOtp) {

		// Check OTP expiry first (avoid unnecessary hash comparison)
		if (user.getOtpExpiryTime() == null || LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
			log.warn("Expired OTP attempt for phone={}", user.getPhoneNumber());
			throw new OtpExpiredException("OTP has expired. Please resend OTP and try again.");
		}

		// Validate OTP purpose and channel
		if (user.getOtpPurpose() == null && user.getOtpChannel() == null) {
			log.warn("OTP purpose missing for user={}", user.getPhoneNumber());
			throw new InvalidOtpPurposeException("OTP purpose is missing.");
		}

		// Validate OTP code
		if (!passwordEncoder.matches(rawOtp, user.getOtp())) {
			log.warn("Invalid OTP attempt for phone={}", user.getPhoneNumber());
			throw new OtpMismatchException("Invalid verification code.");
		}
	}

	/*
	 * Validates the password request.
	 */
	public void validatePassword(String newPassword, String confirmPassword, User user) {

		// Check if new password and confirm password match
		if (!newPassword.equals(confirmPassword)) {
			log.warn("New password and confirm password mismatch.");
			throw new PasswordMismatchException("New password and confirm password do not match.");
		}

		// Ensure the new password is not the same as the old password (if applicable)
		if (user != null && user.getPasswordHash() != null
				&& passwordEncoder.matches(newPassword, user.getPasswordHash())) {
			log.warn("New password matches old password for user: {}", user.getPhoneNumber());
			throw new PasswordValidationException("New password cannot be the same as the old password.");
		}
	}

	/*
	 * Validates the MPIN request.
	 */
	public void validateMpin(String newMPIN, String confirmMPIN, User user) {

		// Validate MPIN length
		if (newMPIN.length() != 4) {
			log.warn("Invalid MPIN length attempt");
			throw new InvalidMpinException("MPIN must be exactly 4 digits.");
		}

		// Validate that new MPIN and confirm MPIN match
		if (!newMPIN.equals(confirmMPIN)) {
			log.warn("New MPIN and confirm MPIN mismatch");
			throw new MpinMismatchException("New MPIN and Confirm MPIN do not match.");
		}

		// Ensure the new MPIN is not the same as the old MPIN (if applicable)

		if (user != null && user.getMpinHash() != null && passwordEncoder.matches(newMPIN, user.getMpinHash())) {
			log.warn("New MPIN matches old MPIN for user: {}", user.getPhoneNumber());
			throw new MpinValidationException("New MPIN cannot be the same as the old MPIN.");
		}
	}

	/*
	 * Validates the user type for the requested operation.
	 */
	private void validateUserType(User user, String requiredUserType) {

		if (!requiredUserType.equals(user.getUserType())) {
			log.warn("User role mismatch for user={}: role={}", user.getPhoneNumber(), user.getUserType());
			throw new UserTypeNotAllowedException("Only " + requiredUserType
					+ " users are allowed to perform this operation. Your user type is: " + user.getUserType());
		}
	}

	/*
	 * Validates the email and username.
	 */

	private void validateEmailAndUsername(User user, String requestEmail, String requestUserName) {

		// Check if the email matches the registered email
		if (!user.getEmail().equals(requestEmail)) {
			log.warn("Email mismatch for user={}: tokenEmail={}, requestEmail={}", user.getPhoneNumber(),
					user.getEmail(), requestEmail);
			throw new EmailConflictException("The provided email does not match the registered email.");

		}

		// Check username availability
		if (userMapper.existsByUserName(requestUserName.trim())) {
			log.warn("Username already taken: {}", requestUserName);
			throw new UserNameAlreadyTakenException("Username is not available. Please choose a different one.");
		}
	}

	/*
	 * Resolves the OTP purpose from the provided value.
	 */

	private OtpPurpose resolveOtpPurpose(String purpose) {
		try {
			return OtpPurpose.valueOf(purpose.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			log.warn("Invalid OTP purpose received: {}", purpose);
			throw new InvalidOtpPurposeException("Invalid OTP purpose provided: " + purpose);

		}
	}

	/*
	 * Resolves the OTP channel from the provided value.
	 */
	private OtpChannel resolveOtpChannel(String channel) {
		try {
			return OtpChannel.valueOf(channel.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			log.warn("Invalid OTP channel received: {}", channel);
			throw new InvalidOtpPurposeException("Invalid OTP channel provided: " + channel);
		}
	}

	/*
	 * Resolves the JWT purpose based on the user's OTP purpose and channel.
	 */
	private JwtPurpose resolveJwtPurpose(User user) {

		if (user == null || user.getOtpPurpose() == null || user.getOtpChannel() == null) {
			throw new InvalidOtpPurposeException("Invalid purpose for forgot flow");
		}

		OtpPurpose purpose = user.getOtpPurpose();
		OtpChannel channel = user.getOtpChannel();

		if (purpose == OtpPurpose.FORGOT_PASSWORD && channel == OtpChannel.EMAIL) {
			return JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL;

		} else if (purpose == OtpPurpose.FORGOT_MPIN && channel == OtpChannel.EMAIL) {
			return JwtPurpose.FORGOT_MPIN_VERIFY_EMAIL;

		} else if (purpose == OtpPurpose.FORGOT_PASSWORD && channel == OtpChannel.PHONE) {
			return JwtPurpose.FORGOT_PASSWORD_VERIFY_PHONE;

		} else if (purpose == OtpPurpose.FORGOT_MPIN && channel == OtpChannel.PHONE) {
			return JwtPurpose.FORGOT_MPIN_VERIFY_PHONE;
		}

		throw new InvalidOtpPurposeException("Invalid purpose for the forgot flow.");

	}

	/*
	 * Sends the OTP through the configured channel (email or phone).
	 */
	private String sendOtpByChannel(User user, String otp) {

		if (user.getOtpChannel() == null) {
			throw new IllegalStateException("OTP channel is not set for the user.");
		}

		if (user.getOtpChannel() == OtpChannel.EMAIL) {

			helper.sendVerificationsOtpToEmail(user.getEmail(), otp, "USER", "Verification Code");

			return "OTP has been resent to your registered email address. It is valid for 5 minutes.";

		} else {

			helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);

			return "OTP has been resent to your registered phone number. It is valid for 5 minutes.";
		}
	}

	/*
	 * Generates a verification JWT token based on the OTP purpose and channel.
	 */
	private String generateVerificationToken(User user, OtpPurpose otpPurpose) {

		if (otpPurpose == null) {
			throw new InvalidOtpPurposeException("OTP purpose cannot be null.");
		}

		if (user.getOtpChannel() == null) {
			throw new IllegalStateException("OTP channel is not set for the user.");
		}

		switch (otpPurpose) {

		case REGISTER:
			return (user.getOtpChannel() == OtpChannel.EMAIL)
					? jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_EMAIL, null)
					: jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_PHONE, null);

		case FORGOT_PASSWORD:
			return (user.getOtpChannel() == OtpChannel.EMAIL)
					? jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL, null)
					: jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PASSWORD_VERIFY_PHONE, null);

		case FORGOT_MPIN:
			return (user.getOtpChannel() == OtpChannel.EMAIL)
					? jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_MPIN_VERIFY_EMAIL, null)
					: jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_MPIN_VERIFY_PHONE, null);

		default:
			throw new InvalidOtpPurposeException("Unsupported OTP purpose: " + otpPurpose);

		}
	}

	/**
	 * Validates the Authorization header, verifies the JWT token, extracts claims,
	 * and resolves the associated User. This method handles only expected
	 * authentication failures (missing header, invalid token, wrong purpose, user
	 * not found) and therefore does not log stack traces
	 */
	private User validateTokenAndGetUser(String authorizationHeader, JwtPurpose... expectedPurposes) {

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
			throw new AccessDeniedException("Invalid token purpose: ");
		}

		// validate actual and expected token purpose
		boolean match = Arrays.stream(expectedPurposes).anyMatch(p -> p == actualPurpose);

		if (!match) {
			throw new AccessDeniedException("Invalid token purpose");
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

	/*
	 * Validates the token and retrieves the user for the forgot email verification
	 * flow.
	 */
	private User validateTokenAndGetUserForForgotVerifyEmail(String authorizationHeader) {
		return validateTokenAndGetUser(authorizationHeader, JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL,
				JwtPurpose.FORGOT_MPIN_VERIFY_EMAIL);
	}

	/*
	 * Validates the token and retrieves the user for the forgot phone OTP flow.
	 */
	private User validateTokenAndGetUserForForgotPhone(String authorizationHeader) {
		return validateTokenAndGetUser(authorizationHeader, JwtPurpose.FORGOT_PASSWORD_SEND_PHONE,
				JwtPurpose.FORGOT_MPIN_SEND_PHONE);
	}

	/*
	 * Validates the token and retrieves the user for the forgot phone verification
	 * flow.
	 */
	private User validateTokenAndGetUserForForgotVerifyPhone(String authorizationHeader) {
		return validateTokenAndGetUser(authorizationHeader, JwtPurpose.FORGOT_PASSWORD_VERIFY_PHONE,
				JwtPurpose.FORGOT_MPIN_VERIFY_PHONE);
	}
}
//END