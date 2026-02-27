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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PublicController handles all endpoints that are accessible without
 * authentication.
 * 
 * These endpoints include, login, registration,password set, MPIN set, test
 * routes, username availability checks, and other public-facing operations like
 * OTP requests or "remember userId".
 * 
 * CORS is enabled for localhost:5500 to allow frontend development access.
 * Logging is provided for request tracing and debugging.
 */
@Tag(name = "APIs", description = "Endpoints accessible such as login, registration, etc")
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
	@Operation(summary = "Public Controller Test endpoint to check if controller is reachable")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {

		log.info("Public test endpoint invoked");

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Public test request completed"));
	}

	/**
	 * Registers a user using phone number. Sends OTP to the provided phone number.
	 */
	@Operation(summary = "Register user using phone number", description = "Registers a user using a phone number and sends an OTP for verification")
	@PostMapping("/register-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> registerPhoneNumber(
			@Valid @RequestBody RegisterPhoneNumberRequest request) {

		log.info("Received phone registration request for phoneNumber={}", request.getPhoneNumber());

		ApiResponse<Map<String, String>> response = userService.registerPhone(request);

		log.info("Phone registration request processed successfully for phoneNumber={}", request.getPhoneNumber());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/*
	 * Verify phone number using OTP.
	 */
	@Operation(summary = "Verify phone number using OTP", description = "Verifies the phone number by validating the OTP sent to the user")
	@PostMapping("/verify-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyPhoneNumber(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("Received phone OTP verification request");

		ApiResponse<Map<String, String>> response = userService.verifyRegistrationPhone(authorizationHeader, request);

		log.info("Phone OTP verification request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Create and set password for verified user
	 */
	@Operation(summary = "Create password for verified user", description = "Creates and sets a password for a user who has successfully verified their phone number")
	@PostMapping("/create-password")
	public ResponseEntity<ApiResponse<Map<String, String>>> createPassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PasswordRequest request) {

		log.info("Received create password request");

		ApiResponse<Map<String, String>> response = userService.createPassword(authorizationHeader, request);

		log.info("Create password request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Check whether a username is already taken or available
	 */
	@Operation(summary = "Check username availability", description = "Checks whether the given username is already taken or available for registration")
	@PostMapping("/check-username")
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	public ResponseEntity<ApiResponse<Void>> checkUsernameAvailability(
			@Valid @RequestBody UserNameCheckRequest request) {

		log.info("Check username availability request received for userName={}", request.getUserName());

		ApiResponse<Void> response = userService.checkUsernameAvailability(request);

		log.info("Check username availability request processed for userName={}", request.getUserName());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Set email and send OTP to email
	 */
	@Operation(summary = "Register email and send OTP", description = "Sets the user's email address and sends an OTP to verify the email")
	@PostMapping("/register-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> registerEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody RegisterEmailRequest request) {

		log.info("Register email request received for email={}", request.getEmail());

		ApiResponse<Map<String, String>> response = userService.registerEmail(authorizationHeader, request);

		log.info("Register email request processed successfully for email={}", request.getEmail());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Verify email through OTP
	 */
	@Operation(summary = "Verify email using OTP", description = "Verifies the user's email address by validating the OTP sent to the email")
	@PostMapping("/verify-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("Verify email OTP request received");

		ApiResponse<Map<String, String>> response = userService.verifyRegistrationEmail(authorizationHeader, request);

		log.info("Verify email OTP request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Set details for NORMAL_USER
	 */
	@Operation(summary = "Set user details", description = "Sets profile details for a verified NORMAL_USER")
	@PostMapping("/set-user-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setUserDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody UserDetailsRequest request) {

		log.info("Set user details request received");

		ApiResponse<Map<String, String>> response = userService.setUserDetails(authorizationHeader, request);

		log.info("Set user details request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Set details for ADVISOR_USER
	 */
	@Operation(summary = "Set advisor details", description = "Sets profile details for a verified ADVISOR_USER")
	@PostMapping("/set-advisor-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setAdvisorDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody AdvisorDetailsRequest request) {

		log.info("Set advisor details request received");

		ApiResponse<Map<String, String>> response = userService.setAdvisorDetails(authorizationHeader, request);

		log.info("Set advisor details request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Set details for BUSINESS_USER
	 */
	@Operation(summary = "Set business details", description = "Sets profile details for a verified BUSINESS_USER")
	@PostMapping("/set-business-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setBusinessDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody BusinessDetailsRequest request) {

		log.info("Set business details request received");

		ApiResponse<Map<String, String>> response = userService.setBusinessDetails(authorizationHeader, request);

		log.info("Set business details request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Set MPIN for user
	 */
	@Operation(summary = "Create MPIN", description = "Creates an MPIN for a verified user after completing the registration flow")
	@PostMapping("/create-mpin")
	public ResponseEntity<ApiResponse<Map<String, String>>> setMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody MpinRequest request) {

		log.info("Set MPIN request received");

		ApiResponse<Map<String, String>> response = userService.createMpin(authorizationHeader, request);

		log.info("Set MPIN request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Login with Email / Phone number and password (returns JWT token if valid)
	 */
	@Operation(summary = "User login", description = "Authenticates a user using email or phone number and password. Returns a JWT token on success.")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, String>>> userLogin(@Valid @RequestBody LoginRequest request) {

		log.info("User login request received");

		ApiResponse<Map<String, String>> response = userService.login(request);

		log.info("User login request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Verify MPIN (TEMP – NOT FOR PROD)
	 */
	@Operation(summary = "Verify MPIN ", description = "Verifies the user's MPIN using the provided authorization token.")
	@PostMapping("/verify-mpin/{mpin}")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyMpinWithToken(
			@RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable("mpin") String mpin) {

		log.info("Verify MPIN request received");

		ApiResponse<Map<String, String>> response = userService.verifyMpin(authorizationHeader, mpin);

		log.info("Verify MPIN request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Get OTP for email
	 */
	@Operation(summary = "Get OTP for email", description = "Sends an OTP to the provided email address for verification or recovery purposes")
	@PostMapping("/forgot/send-email-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> emailOtp(@Valid @RequestBody EmailRequest request) {

		log.info("Get email OTP request received");

		ApiResponse<Map<String, String>> response = userService.sendForgotOtpToEmail(request);

		log.info("Get email OTP request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Verify email OTP for forgot password / MPIN flow
	 */
	@Operation(summary = "Verify forgot email OTP", description = "Verifies the OTP sent to the registered email address for forgot password or MPIN flow")
	@PostMapping("/forgot/verify-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyForgotEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("Verify forgot email OTP request received");

		ApiResponse<Map<String, String>> response = userService.verifyForgotEmail(authorizationHeader, request);

		log.info("Verify forgot email OTP request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Get OTP for phone forgot password
	 */
	@Operation(summary = "Get phone OTP for forgot password", description = "Sends an OTP to the registered phone number for password recovery")
	@PostMapping("/forgot/send-phone-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> phoneNumberOtp(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PhoneNumberRequest request) {

		log.info("Get phone OTP request received for forgot password");

		ApiResponse<Map<String, String>> response = userService.sendForgotOtpToPhone(authorizationHeader, request);

		log.info("Get phone OTP request processed successfully for forgot password");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Verify phone OTP for forgot password / MPIN flow
	 */
	@Operation(summary = "Verify forgot phone OTP", description = "Verifies the OTP sent to the registered phone number for forgot password or MPIN flow")
	@PostMapping("/forgot/verify-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyForgotPhone(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("Verify forgot phone OTP request received");

		ApiResponse<Map<String, String>> response = userService.verifyForgotPhone(authorizationHeader, request);

		log.info("Verify forgot phone OTP request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Verify OTP and reset the password
	 */
	@Operation(summary = "Verify OTP and reset password", description = "Verifies the OTP sent for password recovery and resets the user's password")
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PasswordRequest request) {

		log.info("Reset password request received");

		ApiResponse<Map<String, String>> response = userService.resetPassword(authorizationHeader, request);

		log.info("Reset password request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Verify OTP and reset the MPIN
	 */
	@Operation(summary = "Verify OTP and reset MPIN", description = "Verifies the OTP sent for MPIN recovery and resets the user's MPIN")
	@PostMapping("/reset-mpin")
	public ResponseEntity<ApiResponse<Map<String, String>>> resetMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody MpinRequest request) {

		log.info("Reset MPIN request received");

		ApiResponse<Map<String, String>> response = userService.resetMpin(authorizationHeader, request);

		log.info("Reset MPIN request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Re-send OTP if user didn’t receive it earlier
	 */
	@Operation(summary = "Re-send OTP", description = "Re-sends OTP to the registered phone number or email if the user did not receive it earlier")
	@PostMapping("/re-send-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> reSendOtp(@Valid @RequestBody ReSendOtpRequest request) {

		log.info("Re-send OTP request received");

		ApiResponse<Map<String, String>> response = userService.reSendOtp(request);

		log.info("Re-send OTP request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Change password (if user knows old password → no OTP required)
	 */
	@Operation(summary = "Change password", description = "Allows a logged-in user to change password using the old password. OTP is not required.")
	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse<Void>> changePassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody ChangePasswordRequest request) {

		log.info("Change password request received");

		ApiResponse<Void> response = userService.changePassword(authorizationHeader, request);

		log.info("Change password request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Change MPIN if old MPIN is known
	 */
	@Operation(summary = "Change MPIN", description = "Allows a logged-in user to change MPIN using the existing MPIN. OTP is not required.")
	@PostMapping("/change-mpin")
	public ResponseEntity<ApiResponse<Void>> changeMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody ChangeMpinRequest request) {

		log.info("Change MPIN request received");

		ApiResponse<Void> response = userService.changeMpin(authorizationHeader, request);

		log.info("Change MPIN request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/*
	 * Remember userId by providing registered email / phone number
	 */
	@Operation(summary = "Recover user ID", description = "Helps the user recover their user ID using a registered email address or phone number")
	@PostMapping("/remember-userid")
	public ResponseEntity<ApiResponse<Void>> rememberUserId(@Valid @RequestBody RememberUserNameRequest request) {

		log.info("Remember userId request received");

		ApiResponse<Void> response = userService.rememberUserId(request);

		log.info("Remember userId request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
//END