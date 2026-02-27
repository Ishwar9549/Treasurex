package com.treasurex.userservice.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.treasurex.userservice.entity.ReferalSystem;
import com.treasurex.userservice.entity.User;
import com.treasurex.userservice.exception.InvalidCredentialsException;
import com.treasurex.userservice.exception.ResourceNotFoundException;
import com.treasurex.userservice.helper.Helper;
import com.treasurex.userservice.mapper.UserMapper;
import com.treasurex.userservice.repository.ReferralSystemRepository;
import com.treasurex.userservice.repository.UserRepository;
import com.treasurex.userservice.security.JwtPurpose;
import com.treasurex.userservice.security.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final Helper helper;
	private final PasswordEncoder passwordEncoder;
	private final ReferralSystemRepository referralSystemRepository;
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
		if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
			log.warn("Phone number already registered: {}", request.getPhoneNumber());
			throw new InvalidCredentialsException("This Phone Number Already In Use: " + request.getPhoneNumber());
		}

		// Map request to User entity
		User user = userMapper.registerPhoneNumberRequestToEntity(request);
		log.debug("User entity mapped for phoneNumber={}", request.getPhoneNumber());

		// Generate OTP
		String otp = helper.generateOtp();
		log.debug("OTP generated for phoneNumber={}", request.getPhoneNumber());

		// Set OTP and expiry
		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setOtpPurpose(OtpPurpose.REGISTER_PHONE);

		// Save user
		user = userRepository.save(user);
		log.info("User saved successfully with id={} and phoneNumber={}", user.getId(), user.getPhoneNumber());

		// Send OTP to phone
		helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
		log.info("OTP sent successfully to phoneNumber={}", user.getPhoneNumber());

		// Generate JWT token
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

		JwtPurpose purpose = extractPurpose(authorizationHeader);

		User user = getUserFromTokenHeader(authorizationHeader, purpose);

		// Check OTP expiry first (avoid unnecessary hash comparison)
		if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			log.warn("Expired OTP attempt for phone={}", user.getPhoneNumber());
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		// Validate OTP
		if (!passwordEncoder.matches(request.getOtp(), user.getOtp())) {
			log.warn("Invalid OTP attempt for phone={}", user.getPhoneNumber());
			throw new InvalidCredentialsException("Invalid verification code.");
		}
		if (user.getOtpPurpose() == null) {
			throw new InvalidCredentialsException("OTP purpose is missing");
		}

		// Mark phone as verified
		user.setPhoneVerified(true);
		user.setOtp(null);
		user.setOtpExpiry(null);

		userRepository.save(user);

		String token = null;

		if (user.getOtpPurpose() == OtpPurpose.REGISTER_PHONE) {

			// Generate fresh access token after successful verification for create password
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.CREATE_PASSWORD, null);

		} else if (user.getOtpPurpose() == OtpPurpose.FORGOT_PASSWORD) {

			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.RESET_PASSWORD, null);

		} else if (user.getOtpPurpose() == OtpPurpose.FORGOT_MPIN) {

			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.RESET_MPIN, null);

		}

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("Phone number verified successfully for phone={}", user.getPhoneNumber());

		return ApiResponse.success(data, "OTP verified successfully.");
	}

	/*
	 * Create and set password for verified user
	 */
	@Override
	public ApiResponse<Map<String, String>> createPassword(String authorizationHeader, PasswordRequest request) {

		log.info("Create password service called");

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.CREATE_PASSWORD);

		if (!user.isPhoneVerified()) {
			log.warn("Password creation attempted before phone verification for user");
			throw new InvalidCredentialsException(
					"User Phone Number is not verified. Please verify your phone number before password set");
		}

		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			log.warn("New password and confirm password mismatch");
			throw new InvalidCredentialsException("New Password and Confirm Password do not match");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		// clear OTP data after successful password setup
		user.setOtp(null);
		user.setOtpExpiry(null);

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_EMAIL, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		log.info("Password created successfully for user");

		return ApiResponse.success(data, "Password set successfully.");
	}

	/**
	 * Check if username is available or not (for frontend validation)
	 */
	@Override
	public ApiResponse<Void> checkUsernameAvailability(UserNameCheckRequest request) {

		log.info("Checking username availability for username: {}", request.getUserName());

		boolean exists = userRepository.existsByUserName(request.getUserName().trim());

		if (exists) {
			log.info("Username already taken: {}", request.getUserName());
			return ApiResponse.error(409, "User ID is already taken. Please try a different one.");
		}

		log.info("Username is available: {}", request.getUserName());
		return ApiResponse.success(null, "User ID is available.");
	}

	/*
	 * Set email and send OTP to email
	 */
	@Override
	public ApiResponse<Map<String, String>> registerEmail(String authorizationHeader, RegisterEmailRequest request) {

		log.info("Register email request received for email: {}", request.getEmail());

		if (userRepository.findByEmail(request.getEmail()).isPresent()
				&& userRepository.findByEmail(request.getEmail()).get().isEmailVerified()) {
			log.warn("Attempt to register already verified email: {}", request.getEmail());
			throw new InvalidCredentialsException("This Email Already In Use: " + request.getEmail());
		}

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.REGISTER_EMAIL);

		if (!user.isPhoneVerified()) {
			log.warn("User {} attempted to register email without verified phone", user.getPhoneNumber());
			throw new RuntimeException(
					"User Phone Number is not verified. Please verify your phone number before password set");
		}

		user.setEmail(request.getEmail());

		String otp = helper.generateOtp();

		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
		user.setOtpPurpose(OtpPurpose.REGISTER_EMAIL);

		helper.sendOtpForEmailVerification(request.getEmail(), otp, "USER");

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.REGISTER_VERIFY_EMAIL, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "OTP sent to mail successfully.";

		log.info("OTP sent successfully to email: {}", request.getEmail());

		return ApiResponse.success(data, message);
	}

	/*
	 * Verify email through OTP
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyEmail(String authorizationHeader, OtpVerifyRequest request) {

		log.info("Verify email OTP request received for authorization header: {}", authorizationHeader);

		JwtPurpose purpose = extractPurpose(authorizationHeader);

		User user = getUserFromTokenHeader(authorizationHeader, purpose);

		if (user.getEmail() == null) {
			throw new RuntimeException("Email is not registered yet. Please register your email.");
		}

		if (!user.isPhoneVerified()) {
			throw new RuntimeException(
					"User Phone Number is not verified. Please verify your phone number before email setup");
		}

		if (user.getOtpPurpose() == null) {
			throw new InvalidCredentialsException("OTP purpose is missing");
		}

		if (!passwordEncoder.matches(request.getOtp(), user.getOtp())) {
			throw new InvalidCredentialsException("Invalid verification code.");
		}

		if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
			throw new InvalidCredentialsException("OTP has expired. Please resend OTP and try again.");
		}

		user.setEmailVerified(true);
		user.setOtp(null);
		user.setOtpExpiry(null);

		userRepository.save(user);

		String token;
		if (user.getOtpPurpose() == OtpPurpose.FORGOT_PASSWORD) {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PHONE, null);
		} else {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_DETAILS, null);
		}

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "OTP Verified successfully.";

		log.info("Email verified successfully for user: {}", user.getPhoneNumber());

		return ApiResponse.success(data, message);
	}

	/*
	 * Set details for NORMAL_USER
	 */
	@Override
	public ApiResponse<Map<String, String>> setUserDetails(String authorizationHeader, UserDetailsRequest request) {

		log.info("SetUserDetails request received for authorization header: {}", authorizationHeader);

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.SET_DETAILS);

		if (user.getEmail() == null) {
			throw new RuntimeException("Email is not Verified. Please verify your email first");
		}

		if (!user.getEmail().equals(request.getEmail())) {
			throw new RuntimeException("Email conflict Error");
		}

		if (userRepository.existsByUserName(request.getUserName().trim())) {
			throw new RuntimeException("User Name is not Available. try Different");
		}

		String role = user.getTypeOfUser();

		if (!role.equals("NORMAL_USER")) {
			throw new ResourceNotFoundException("You are " + role + ". Only NORMAL_USER is allowed for this operation");
		}

		user = userMapper.setUserDetailsRequestToEntity(user, request);

		referSystem(request.getReferralCode(), user);

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "User details set successfully.";

		log.info("SetUserDetails completed successfully for user: {}", user.getPhoneNumber());

		return ApiResponse.success(data, message);
	}

	/*
	 * Set details for ADVIOSOR_USER
	 */
	@Override
	public ApiResponse<Map<String, String>> setAdvisorDetails(String authorizationHeader,
			AdvisorDetailsRequest request) {

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.SET_DETAILS);

		if (user.getEmail() == null) {
			throw new RuntimeException("Email is not Verified. Please verify your email first");
		}

		if (!user.getEmail().equals(request.getEmail())) {
			throw new RuntimeException("Email conflict Error");
		}

		if (userRepository.existsByUserName(request.getUserName().trim())) {
			throw new RuntimeException("User Name is not Available. try Different");
		}

		String role = user.getTypeOfUser();

		if (!role.equals("ADVISOR_USER")) {
			throw new ResourceNotFoundException("You are " + role + " Not allow for this only ADVISOR_USER allowed");
		}

		user = userMapper.setAdvisorDetailsRequestToEntity(user, request);

		referSystem(request.getReferralCode(), user);

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "Advisor details set successfully.";

		return ApiResponse.success(data, message);
	}

	/*
	 * Set details for BUSINESS_USER
	 */
	@Override
	public ApiResponse<Map<String, String>> setBusinessDetails(String authorizationHeader,
			BusinessDetailsRequest request) {

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.SET_DETAILS);

		if (user.getEmail() == null) {
			throw new RuntimeException("Email is not Verified. Please verify your email first");
		}

		if (!user.getEmail().equals(request.getEmail())) {
			throw new RuntimeException("Email conflict Error");
		}

		if (userRepository.existsByUserName(request.getUserName().trim())) {
			throw new RuntimeException("User Name is not Available. try Different");
		}

		String role = user.getTypeOfUser();

		if (!role.equals("BUSINESS_USER")) {
			throw new ResourceNotFoundException("You are " + role + " Not allow for this only BUSINESS_USER allowed");
		}

		user = userMapper.setBusinessDetailsRequestToEntity(user, request);

		referSystem(request.getReferralCode(), user);

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.SET_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "Business details set successfully.";

		return ApiResponse.success(data, message);
	}

	/*
	 * Set MPIN for user
	 */
	@Override
	public ApiResponse<Map<String, String>> setMpin(String authorizationHeader, MpinRequest request) {

		log.info("Set MPIN request received");

		// Validate MPIN length
		if (request.getNewMpin().length() != 4) {
			throw new InvalidCredentialsException("MPIN should have 4 digits");
		}

		// Validate MPIN confirmation
		if (!request.getNewMpin().equals(request.getConfirmMpin())) {
			throw new InvalidCredentialsException("MPIN and Confirm MPIN do not match");
		}

		// Retrieve user from token
		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.SET_MPIN);

		// Ensure phone and email are verified
		if (!user.isPhoneVerified()) {
			throw new InvalidCredentialsException("User phone is not verified. Please verify your phone first.");
		}

		if (!user.isEmailVerified()) {
			throw new InvalidCredentialsException("User email is not verified. Please verify your email first.");
		}

		// Save hashed MPIN
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));
		userRepository.save(user);

		log.info("MPIN set successfully for user: {}", user.getPhoneNumber());

		// Generate new JWT token
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

		log.info("Login attempt for identifier: {}", loginRequest.getLoginId());

		User user = findUserByLoginId(loginRequest.getLoginId());

		if (!user.isPhoneVerified()) {
			throw new RuntimeException("User Phone is not verified. Please verify your Phone first.");
		}

		if (!user.isEmailVerified()) {
			throw new RuntimeException("User Email is not verified. Please verify your Email first.");
		}

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password.");
		}

		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_MPIN, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token); // fixed the key typo from "token." to "token"

		String message = "Login Successful";

		log.info("Login successful for user: {}", loginRequest.getLoginId());

		return ApiResponse.success(data, message);
	}

	/**
	 * Verify MPIN for the given user.
	 */
	@Override
	public ApiResponse<Map<String, String>> verifyMpin(String authorizationHeader, String mpin) {

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.ACCESS_MPIN);

		if (!passwordEncoder.matches(mpin, user.getMpinHash())) {
			throw new InvalidCredentialsException("Invalid MPIN.");
		}

		String token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.ACCESS_PROFILE, null);

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token); // fixed the key typo from "token." to "token"

		String message = "MPIN Verified successfully.";

		return ApiResponse.success(data, message);
	}

	/**
	 * get OTP to Email
	 */
	@Override
	public ApiResponse<Map<String, String>> getEmailOtp(EmailRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with Email: " + request.getEmail()));

		String otp = helper.generateOtp();

		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

		if (request.getPurpose().equals("FORGOT_PASSWORD")) {
			user.setOtpPurpose(OtpPurpose.FORGOT_PASSWORD);

		}
		if (request.getPurpose().equals("FORGOT_MPIN")) {
			user.setOtpPurpose(OtpPurpose.FORGOT_MPIN);
		}

		user = userRepository.save(user);

		helper.sendOtpForEmailVerification(request.getEmail(), otp, "");

		userRepository.save(user);

		String token = null;

		if (user.getOtpPurpose() == OtpPurpose.FORGOT_PASSWORD) {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL, null);
		}
		if (user.getOtpPurpose() == OtpPurpose.FORGOT_MPIN) {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_MPIN_VERIFY_EMAIL, null);
		}

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "OTP sent to mail successfully.";

		return ApiResponse.success(data, message);
	}

	/*
	 * Get phone OTP
	 */
	@Override
	public ApiResponse<Map<String, String>> getPhoneNumberOtp(String authorizationHeader, PhoneNumberRequest request) {

		JwtPurpose purpose = extractPurpose(authorizationHeader);

		User user = getUserFromTokenHeader(authorizationHeader, purpose);

		if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {

			throw new InvalidCredentialsException("Email and Phone number is not matching..");
		}

		String otp = helper.generateOtp();

		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

		if (request.getPurpose() == OtpPurpose.FORGOT_PASSWORD.toString()) {
			user.setOtpPurpose(OtpPurpose.FORGOT_PASSWORD);

		}
		if (request.getPurpose() == OtpPurpose.FORGOT_MPIN.toString()) {
			user.setOtpPurpose(OtpPurpose.FORGOT_MPIN);
		}

		helper.sendVerificationOtpToPhone(request.getPhoneNumber(), otp);

		user = userRepository.save(user);

		String token = null;

		if (user.getOtpPurpose() == OtpPurpose.FORGOT_PASSWORD) {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_PASSWORD_VERIFY_PHONE, null);
		}
		if (user.getOtpPurpose() == OtpPurpose.FORGOT_MPIN) {
			token = jwtUtil.generateToken(user.getPhoneNumber(), JwtPurpose.FORGOT_MPIN_VERIFY_PHONE, null);
		}

		Map<String, String> data = new LinkedHashMap<>();
		data.put("token", token);

		String message = "OTP sent to Phone successfully.";

		return ApiResponse.success(data, message);
	}

	/*
	 * Reset Password
	 */
	@Override
	public ApiResponse<Map<String, String>> resetPassword(String authorizationHeader, PasswordRequest request) {

		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new InvalidCredentialsException("New Password and Confirm Password do not match");
		}

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.RESET_PASSWORD);

		if (!user.isPhoneVerified()) {
			throw new RuntimeException("User Phone is not verified. Please verify your Phone first.");
		}

		if (!user.isEmailVerified()) {
			throw new RuntimeException("User Email is not verified. Please verify your Email first.");
		}

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Password should not be same as Old password.");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		user.setOtp(null);
		user.setOtp(null);
		user.setOtpExpiry(null);

		userRepository.save(user);

		String message = "PASSWORD_RESET_SUCCESS";

		return ApiResponse.success(null, message);
	}

	/*
	 * Reset Password
	 */
	@Override
	public ApiResponse<Map<String, String>> resetMpin(String authorizationHeader, MpinRequest request) {

		if (!request.getNewMpin().equals(request.getConfirmMpin())) {
			throw new InvalidCredentialsException("New MPIN and Confirm Password do not match");
		}

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.RESET_MPIN);

		if (!user.isPhoneVerified()) {
			throw new RuntimeException("User Phone is not verified. Please verify your Phone first.");
		}

		if (!user.isEmailVerified()) {
			throw new RuntimeException("User Email is not verified. Please verify your Email first.");
		}

		if (passwordEncoder.matches(request.getNewMpin(), user.getMpinHash())) {
			throw new InvalidCredentialsException("MPIN should not be same as Old MPIN.");
		}

		String message = "MPIN_RESET_SUCCESS";
		return ApiResponse.success(null, message);
	}
	/*
	 * inside UserServiceImp helper methods
	 */

	private static final String BEARER_PREFIX = "Bearer ";

	private JwtPurpose extractPurpose(String authorizationHeader) {

		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new InvalidCredentialsException("Authorization header is missing");
		}

		if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
			throw new InvalidCredentialsException("Invalid Authorization header");
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

		if (token.isEmpty()) {
			throw new InvalidCredentialsException("Bearer token is empty");
		}

		if (!jwtUtil.validateToken(token)) {
			throw new InvalidCredentialsException("Invalid or expired token");
		}

		Claims claims = jwtUtil.extractAllClaims(token);

		String purposeClaim = claims.get("purpose", String.class);
		if (purposeClaim == null) {
			throw new InvalidCredentialsException("Token purpose is missing");
		}

		try {
			return JwtPurpose.valueOf(purposeClaim);
		} catch (IllegalArgumentException ex) {
			throw new InvalidCredentialsException("Invalid token purpose");
		}
	}

	/**
	 * Resends OTP for account verification or credential recovery.
	 */
	@Override
	public ApiResponse<Map<String, String>> reSendOtp(ReSendOtpRequest request) {

		User user = findUserByLoginId(request.getLoginId());

		String otp = helper.generateOtp();
		user.setOtp(passwordEncoder.encode(otp));
		user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

		OtpPurpose otpPurpose;
		try {
			otpPurpose = OtpPurpose.valueOf(request.getOtpPurpose());
		} catch (IllegalArgumentException ex) {
			throw new InvalidCredentialsException("Invalid OTP purpose: " + request.getOtpPurpose());
		}

		user.setOtpPurpose(otpPurpose);
		userRepository.save(user);

		String message;

		switch (otpPurpose) {

		case REGISTER_EMAIL:
			helper.sendOtpForEmailVerification(user.getEmail(), otp, "");
			message = "OTP has been resent to your registered email address. It is valid for 5 minutes.";
			break;

		case REGISTER_PHONE:
			helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
			message = "OTP has been resent to your registered phone number. It is valid for 5 minutes.";
			break;

		case FORGOT_PASSWORD_EMAIL:
			helper.sendOtpForEmailVerification(user.getEmail(), otp, "");
			message = "OTP has been resent to your registered email address for password reset. It is valid for 5 minutes.";
			break;

		case FORGOT_PASSWORD_PHONE:
			helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
			message = "OTP has been resent to your registered phone number for password reset. It is valid for 5 minutes.";
			break;

		case FORGOT_MPIN_EMAIL:
			helper.sendOtpForEmailVerification(user.getEmail(), otp, "");
			message = "OTP has been resent to your registered email address for MPIN reset. It is valid for 5 minutes.";
			break;

		case FORGOT_MPIN_PHONE:
			helper.sendVerificationOtpToPhone(user.getPhoneNumber(), otp);
			message = "OTP has been resent to your registered phone number for MPIN reset. It is valid for 5 minutes.";
			break;

		default:
			throw new InvalidCredentialsException("Unsupported OTP purpose");
		}

		return ApiResponse.success(null, message);
	}

	/**
	 * Changes user password after validating old password.
	 */
	@Override
	public ApiResponse<Void> changePassword(String authorizationHeader, ChangePasswordRequest changePasswordRequest) {

		if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
			throw new InvalidCredentialsException("New Password and Confirm Password do not match");
		}

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.ACCESS_PROFILE);

		if (!user.isPhoneVerified() && !user.isEmailVerified()) {
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
	 * change MPIN for the given user if you know old MPIN.
	 */
	@Override
	public ApiResponse<Void> changeMpin(String authorizationHeader, ChangeMpinRequest request) {

		User user = getUserFromTokenHeader(authorizationHeader, JwtPurpose.ACCESS_PROFILE);

		if (!(request.getOldMpin().length() == 4) || !(request.getNewMpin().length() == 4)
				|| !(request.getConfirmMpin().length() == 4)) {
			throw new RuntimeException("MPIN should have 4 digits");
		}

		if (!request.getNewMpin().equals(request.getConfirmMpin())) {
			throw new InvalidCredentialsException("New MPIN and Confirm MPIN  miss match");
		}

		if (!user.isPhoneVerified() && !user.isEmailVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account first.");
		}
		if (!passwordEncoder.matches(request.getOldMpin(), user.getMpinHash())) {
			throw new InvalidCredentialsException("Old MPIN is incorrect.");
		}
		if (passwordEncoder.matches(request.getNewMpin(), user.getMpinHash())) {
			throw new InvalidCredentialsException("MPIN should not be same as Old MPIN.");
		}
		user.setMpinHash(passwordEncoder.encode(request.getNewMpin()));
		userRepository.save(user);

		String message = "MPIN changed successfully.";

		return ApiResponse.success(null, message);
	}

	/**
	 * Retrieves the user ID associated with a given login identifier.
	 */
	@Override
	public ApiResponse<Void> rememberUserId(RememberUserNameRequest rememberUserIdRequest) {

		User user = findUserByLoginId(rememberUserIdRequest.getLoginId());

		String message = "Your user Id is : ISH123" + user.getUserName();
		return ApiResponse.success(null, message);
	}

	/**
	 * Extracts user from Authorization header containing a REGISTRATION Bearer
	 * token.
	 */
	private User getUserFromTokenHeader(String authorizationHeader, JwtPurpose expectedPurpose) {

		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			log.warn("Authorization header is missing");
			throw new InvalidCredentialsException("Authorization header is missing");
		}

		if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
			log.warn("Authorization header does not start with Bearer");
			throw new InvalidCredentialsException("Invalid Authorization header");
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

		if (token.isEmpty()) {
			log.warn("Bearer token is empty");
			throw new InvalidCredentialsException("Bearer token is empty");
		}

		if (!jwtUtil.validateToken(token)) {
			log.warn("Invalid or expired JWT token");
			throw new InvalidCredentialsException("Invalid or expired token");
		}

		Claims claims;
		try {
			claims = jwtUtil.extractAllClaims(token);
		} catch (Exception e) {
			log.error("Failed to extract JWT claims", e);
			throw new InvalidCredentialsException("Invalid token claims");
		}

		String purposeClaim = claims.get("purpose", String.class);
		if (purposeClaim == null) {
			log.warn("JWT purpose claim is missing");
			throw new InvalidCredentialsException("Token purpose is missing");
		}

		JwtPurpose actualPurpose;
		try {
			actualPurpose = JwtPurpose.valueOf(purposeClaim);
		} catch (IllegalArgumentException e) {
			log.warn("Invalid JWT purpose value: {}", purposeClaim);
			throw new InvalidCredentialsException("Invalid token purpose");
		}

		if (actualPurpose != expectedPurpose) {
			log.warn("Invalid token purpose: expected {}, found {}", expectedPurpose, actualPurpose);
			throw new InvalidCredentialsException("Invalid token purpose");
		}

		String phoneNumber = claims.getSubject();
		if (phoneNumber == null || phoneNumber.isBlank()) {
			log.warn("JWT subject is missing");
			throw new InvalidCredentialsException("Token subject is missing");
		}

		return userRepository.findByPhoneNumber(phoneNumber)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with phone number: " + phoneNumber));
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
			return userRepository.findByUserName(loginId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with User Name " + loginId));
		}
	}

	/*
	 * 
	 */

	private void referSystem(String referralCode, User user) {

		// 1. Validate referral code (if provided)
		if (referralCode != null && !referralCode.equalsIgnoreCase("NA")) {
			referralSystemRepository.findByReferralCode(referralCode).orElseThrow(
					() -> new InvalidCredentialsException("Referral Code is invalid, use NA if no referral code"));
		}

		// 2. Ensure THIS USER does not already have a referral record
		if (referralSystemRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
			return; // already exists → do nothing
		}

		// 3. Create referral record for current user
		ReferalSystem newReferral = ReferalSystem.builder().phoneNumber(user.getPhoneNumber())
				.referralCode(user.getUserName())
				.referredBy((referralCode == null || referralCode.equalsIgnoreCase("NA")) ? null : referralCode)
				.referralCount(0).referralBonus(0).user(user) // ONLY here user is set
				.build();

		// 4. Update referrer stats ONLY (no user reassignment)
		if (referralCode != null && !referralCode.equalsIgnoreCase("NA")) {
			referralSystemRepository.findByReferralCode(referralCode).ifPresent(referrer -> {
				referrer.setReferralCount(referrer.getReferralCount() + 1);
				referrer.setReferralBonus(referrer.getReferralBonus() + 10);
				referralSystemRepository.save(referrer);
			});
		}

		// 5. Save referral for this user (ONLY ONCE)
		referralSystemRepository.save(newReferral);
	}
}