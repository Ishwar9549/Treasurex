package com.treasurex.userservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.treasurex.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

	private final UserService userService;

	/*
	 * Test End point to check if controller is reachable
	 */
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Public test request completed"));
	}

	/**
	 * Registers a user using phone number. Sends OTP to the provided phone number.
	 */
	@PostMapping("/register-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> registerPhoneNumber(
			@Valid @RequestBody RegisterPhoneNumberRequest request) {

		log.info("Received phone registration request for phoneNumber={}", request.getPhoneNumber());

		ApiResponse<Map<String, String>> response = userService.registerPhoneNumber(request);

		log.info("Phone registration request processed successfully for phoneNumber={}", request.getPhoneNumber());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/*
	 * Verify phone number using OTP.
	 */
	@PostMapping("/verify-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyPhoneNumber(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("Verify phone OTP request received");

		ApiResponse<Map<String, String>> response = userService.verifyPhoneNumber(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Create and set password for verified user
	 */
	@PostMapping("/create-password")
	public ResponseEntity<ApiResponse<Map<String, String>>> createPassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PasswordRequest request) {

		log.info("Create password request received");

		ApiResponse<Map<String, String>> response = userService.createPassword(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}

	/**
	 * Check whether a username is already taken or available
	 */
	@PostMapping("/check-username")
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	public ResponseEntity<ApiResponse<Void>> checkUsernameAvailability(
			@Valid @RequestBody UserNameCheckRequest request) {

		log.info("Check username availability request received");

		ApiResponse<Void> response = userService.checkUsernameAvailability(request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Set email and send OTP to email
	 */
	@PostMapping("/register-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> registerEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody RegisterEmailRequest request) {

		log.info("Register email request received for email: {}", request.getEmail());

		ApiResponse<Map<String, String>> response = userService.registerEmail(authorizationHeader, request);

		log.info("Email registration process completed for email: {}", request.getEmail());

		return ResponseEntity.ok(response);
	}

	/*
	 * Verify email through OTP
	 */
	@PostMapping("/verify-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("Verify email OTP request received");

		ApiResponse<Map<String, String>> response = userService.verifyEmail(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Set details for NORMAL_USER
	 */
	@PostMapping("/set-user-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setUserDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody UserDetailsRequest request) {

		log.info("Set user details request received for authorization header: {}", authorizationHeader);

		ApiResponse<Map<String, String>> response = userService.setUserDetails(authorizationHeader, request);

		log.info("Set user details response generated for user");

		return ResponseEntity.ok(response);
	}

	/*
	 * Set details for ADVISOR_USER
	 */
	@PostMapping("/set-advisor-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setAdvisorDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody AdvisorDetailsRequest request) {

		log.info("SetAdvisorDetails request received for authorization header: {}", authorizationHeader);

		ApiResponse<Map<String, String>> response = userService.setAdvisorDetails(authorizationHeader, request);

		log.info("SetAdvisorDetails completed successfully for user: {}", request.getUserName());

		return ResponseEntity.ok(response);
	}

	/*
	 * Set details for BUSINESS_USER
	 */
	@PostMapping("/set-business-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setBusinessDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody BusinessDetailsRequest request) {

		log.info("SetBusinessDetails request received for user: {}", request.getUserName());

		ApiResponse<Map<String, String>> response = userService.setBusinessDetails(authorizationHeader, request);

		log.info("SetBusinessDetails completed successfully for user: {}", request.getUserName());

		return ResponseEntity.ok(response);
	}

	/*
	 * Set MPIN for user
	 */
	@PostMapping("/create-mpin")
	public ResponseEntity<ApiResponse<Map<String, String>>> setMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody MpinRequest request) {

		log.info("Set MPIN request received for user");

		ApiResponse<Map<String, String>> response = userService.setMpin(authorizationHeader, request);

		log.info("Set MPIN completed successfully for user");

		return ResponseEntity.ok(response);
	}

	/*
	 * Login with Email / Phone number and password (returns JWT token if valid)
	 */
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, String>>> userLogin(@Valid @RequestBody LoginRequest request) {

		log.info("User login request received for identifier: {}",
				request.getLoginId() != null ? request.getLoginId() : request.getLoginId());

		ApiResponse<Map<String, String>> response = userService.login(request);

		log.info("Login processed successfully for identifier: {}",
				request.getLoginId() != null ? request.getLoginId() : request.getLoginId());

		return ResponseEntity.ok(response);
	}

	/*
	 * Verify MPIN
	 */
	@PostMapping("/verify-mpin/{mpin}")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyMpinWithToken(
			@RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable("mpin") String mpin) {

		log.info("Verify MPIN request received");

		ApiResponse<Map<String, String>> response = userService.verifyMpin(authorizationHeader, mpin);

		return ResponseEntity.ok(response);
	}

	/*
	 * Get OTP for email
	 */
	@PostMapping("/get-email-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> emailOtp(@Valid @RequestBody EmailRequest request) {

		log.info("Get email OTP request received");

		ApiResponse<Map<String, String>> response = userService.getEmailOtp(request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Get OTP for phone forgot password
	 */
	@PostMapping("/get-phone-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> phoneNumberOtp(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PhoneNumberRequest request) {

		log.info("Get phone OTP request received for forgot password");

		ApiResponse<Map<String, String>> response = userService.getPhoneNumberOtp(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Verify OTP and reset the password
	 */
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PasswordRequest request) {

		log.info("Reset password request received");

		ApiResponse<Map<String, String>> response = userService.resetPassword(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Verify OTP and reset the password
	 */
	@PostMapping("/reset-mpin")
	public ResponseEntity<ApiResponse<Map<String, String>>> resetMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody MpinRequest request) {

		log.info("Reset password request received");

		ApiResponse<Map<String, String>> response = userService.resetMpin(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Re-send OTP if user didn’t receive it earlier
	 */
	@PostMapping("/re-send-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> reSendOtp(@Valid @RequestBody ReSendOtpRequest request) {

		log.info("Re-send OTP request received");

		ApiResponse<Map<String, String>> response = userService.reSendOtp(request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Change password (if user knows old password → no OTP required)
	 */
	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse<Void>> changePassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody ChangePasswordRequest request) {

		log.info("Change password request received");

		ApiResponse<Void> response = userService.changePassword(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}

	/*
	 * Change MPIN if old MPIN is known
	 */
	@PostMapping("/change-mpin")
	public ResponseEntity<ApiResponse<Void>> changeMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody ChangeMpinRequest request) {

		log.info("Change MPIN request received");

		ApiResponse<Void> response = userService.changeMpin(authorizationHeader, request);

		return ResponseEntity.ok(response);
	}
	

	/*
	 * Remember userId by providing registered email / phone number
	 */
	@PostMapping("/remember-userid")
	public ResponseEntity<ApiResponse<Void>> rememberUserId(@Valid @RequestBody RememberUserNameRequest request) {

		log.info("Remember userId request received");

		ApiResponse<Void> response = userService.rememberUserId(request);

		return ResponseEntity.ok(response);
	}
}
