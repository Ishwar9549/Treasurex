package com.treasurex.userservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.treasurex.userservice.dto.VerifyMpinRequest;
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

	/**
	 * Test endpoint to check if PublicController is reachable.
	 */
	@Operation(summary = "Public Controller test endpoint to verify accessibility")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {

		log.info("[PublicController /test] Endpoint invoked");

		return ResponseEntity.ok(ApiResponse.success(null, "Public test request completed successfully"));
	}

	/**
	 * Registers a new user using their phone number and sends an OTP for
	 * verification.
	 */
	@Operation(summary = "Register a user with phone number", description = "Registers a new user using a phone number and sends a one-time password (OTP) for verification.")
	@PostMapping("/register-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> registerPhoneNumber(
			@Valid @RequestBody RegisterPhoneNumberRequest request) {

		log.info("[PublicController /register-phone] Received registration request for phoneNumber={}",
				request.getPhoneNumber());

		ApiResponse<Map<String, String>> response = userService.registerPhone(request);

		log.info("[PublicController /register-phone] Registration request completed successfully for phoneNumber={}",
				request.getPhoneNumber());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Verifies a user's phone number using a one-time password (OTP).
	 */
	@Operation(summary = "Verify phone number using OTP", description = "Validates the OTP sent to the user's phone number to confirm registration.")
	@PostMapping("/register/verify-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyPhoneNumber(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("[PublicController /register/verify-phone] Received OTP verification request for phone number");

		ApiResponse<Map<String, String>> response = userService.verifyRegistrationPhone(authorizationHeader, request);

		log.info("[PublicController /register/verify-phone] OTP verification completed successfully for phone number");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Creates and sets a password for a user who has successfully verified their
	 * phone number.
	 */
	@Operation(summary = "Set password for verified user", description = "Allows a verified user to create and set a password for their account.")
	@PostMapping("/create-password")
	public ResponseEntity<ApiResponse<Map<String, String>>> createPassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PasswordRequest request) {

		log.info("[PublicController /create-password] Received request to set password for verified user");

		ApiResponse<Map<String, String>> response = userService.createPassword(authorizationHeader, request);

		log.info("[PublicController /create-password] Password creation completed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Checks whether a username is available or already taken.
	 */
	@Operation(summary = "Check username availability", description = "Determines if the specified username is available for registration or already in use.")
	@PostMapping("/check-username")
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	public ResponseEntity<ApiResponse<Void>> checkUsernameAvailability(
			@Valid @RequestBody UserNameCheckRequest request) {

		log.info("[PublicController /check-username] Username availability check request received for userName={}",
				request.getUserName());

		ApiResponse<Void> response = userService.checkUsernameAvailability(request);

		log.info(
				"[PublicController /check-username] Username availability check processed successfully for userName={}",
				request.getUserName());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Registers the user's email and sends an OTP for verification.
	 */
	@Operation(summary = "Register email and send OTP", description = "Sets the user's email address and sends a one-time password (OTP) for email verification.")
	@PostMapping("/register-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> registerEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody RegisterEmailRequest request) {

		log.info("[PublicController /register-email] Email registration request received for email={}",
				request.getEmail());

		ApiResponse<Map<String, String>> response = userService.registerEmail(authorizationHeader, request);

		log.info("[PublicController /register-email] Email registration request processed successfully for email={}",
				request.getEmail());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Verifies the user's email address using the OTP sent to their email.
	 */
	@Operation(summary = "Verify email using OTP", description = "Validates the OTP sent to the user's email and confirms their email address.")
	@PostMapping("/register/verify-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info(
				"[PublicController /register/verify-email] Email OTP verification request received for email verification.");

		ApiResponse<Map<String, String>> response = userService.verifyRegistrationEmail(authorizationHeader, request);

		log.info("[PublicController /register/verify-email] Email OTP verification processed successfully.");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Sets profile details for a verified NORMAL_USER.
	 */
	@Operation(summary = "Set user details", description = "Updates the profile information for a user with NORMAL_USER role after verification.")
	@PostMapping("/set-user-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setUserDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody UserDetailsRequest request) {

		log.info("[PublicController /set-user-details] Received request to set user details for NORMAL_USER.");

		ApiResponse<Map<String, String>> response = userService.setUserDetails(authorizationHeader, request);

		log.info("[PublicController /set-user-details] User details updated successfully.");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Sets profile details for a verified ADVISOR_USER.
	 */
	@Operation(summary = "Set advisor details", description = "Updates the profile information for a user with the ADVISOR_USER role after verification.")
	@PostMapping("/set-advisor-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setAdvisorDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody AdvisorDetailsRequest request) {

		log.info("[PublicController /set-advisor-details] Received request to set advisor details.");

		ApiResponse<Map<String, String>> response = userService.setAdvisorDetails(authorizationHeader, request);

		log.info("[PublicController /set-advisor-details] Advisor details updated successfully.");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Sets profile details for a verified BUSINESS_USER.
	 */
	@Operation(summary = "Set business details", description = "Updates the profile information for a user with the BUSINESS_USER role after verification.")
	@PostMapping("/set-business-details")
	public ResponseEntity<ApiResponse<Map<String, String>>> setBusinessDetails(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody BusinessDetailsRequest request) {

		log.info("[PublicController /set-business-details] Received request to set business details.");

		ApiResponse<Map<String, String>> response = userService.setBusinessDetails(authorizationHeader, request);

		log.info("[PublicController /set-business-details] Business details updated successfully.");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Creates an MPIN for a verified user after completing the registration
	 * process.
	 */
	@Operation(summary = "Create MPIN", description = "Generates and sets a secure 4-digit MPIN for a user who has successfully verified their account.")
	@PostMapping("/create-mpin")
	public ResponseEntity<ApiResponse<Map<String, String>>> setMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody MpinRequest request) {

		log.info("[PublicController /create-mpin] Received request to set MPIN.");

		ApiResponse<Map<String, String>> response = userService.createMpin(authorizationHeader, request);

		log.info("[PublicController /create-mpin] MPIN created successfully.");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Authenticates a user using email or phone number and password. Returns a JWT
	 * token upon successful authentication.
	 */
	@Operation(summary = "User login", description = "Logs in a user with email or phone number and password. Returns a JWT token on successful authentication.")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, String>>> userLogin(@Valid @RequestBody LoginRequest request) {

		log.info("[PublicController /login] Login request received for identifier={}", request.getLoginId());

		ApiResponse<Map<String, String>> response = userService.login(request);

		log.info("[PublicController /login] Login processed successfully for identifier={}", request.getLoginId());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Verifies the user's MPIN using the provided authorization token. TEMPORARY
	 * endpoint – not intended for production use.
	 */
	@Operation(summary = "Verify MPIN", description = "Verifies a user's MPIN using the provided authorization token. This endpoint is for testing purposes only.")
	@PostMapping("/verify-mpin")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyMpinWithToken(
			@Valid @RequestBody VerifyMpinRequest request,
			@RequestHeader(name = "Authorization") String authorizationHeader) {

		log.info("[PublicController /verify-mpin] MPIN verification request received");

		ApiResponse<Map<String, String>> response = userService.verifyMpin(authorizationHeader, request);

		log.info("[PublicController /verify-mpin] MPIN verification processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Sends a one-time password (OTP) to the specified email address. This can be
	 * used for account verification or credential recovery.
	 */
	@Operation(summary = "Send OTP to email", description = "Sends a one-time password (OTP) to the specified email address for verification or password/MPIN recovery.")
	@PostMapping("/forgot/send-email-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> emailOtp(@Valid @RequestBody EmailRequest request) {

		log.info("[PublicController /forgot/send-email-otp] OTP request received for email={}", request.getEmail());

		ApiResponse<Map<String, String>> response = userService.sendForgotOtpToEmail(request);

		log.info("[PublicController /forgot/send-email-otp] OTP request processed successfully for email={}",
				request.getEmail());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Verifies the OTP sent to a registered email for forgot password or MPIN
	 * flows.
	 */
	@Operation(summary = "Verify OTP for forgot password/MPIN", description = "Validates the OTP sent to the registered email address during the forgot password or forgot MPIN process.")
	@PostMapping("/forgot/verify-email")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyForgotEmail(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("[PublicController /forgot/verify-email] OTP verification request received for email");

		ApiResponse<Map<String, String>> response = userService.verifyForgotEmail(authorizationHeader, request);

		log.info("[PublicController /forgot/verify-email] OTP verification processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Sends an OTP to the registered phone number for forgot password or MPIN
	 * recovery.
	 */
	@Operation(summary = "Send OTP to phone for password recovery", description = "Generates and sends an OTP to the user's registered phone number for forgot password or MPIN recovery.")
	@PostMapping("/forgot/send-phone-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> phoneNumberOtp(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PhoneNumberRequest request) {

		log.info("[PublicController /forgot/send-phone-otp] OTP request received for phone number: {}",
				request.getPhoneNumber());

		ApiResponse<Map<String, String>> response = userService.sendForgotOtpToPhone(authorizationHeader, request);

		log.info("[PublicController /forgot/send-phone-otp] OTP sent successfully to phone number: {}",
				request.getPhoneNumber());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Verifies the OTP sent to the registered phone number for forgot password or
	 * MPIN recovery.
	 */
	@Operation(summary = "Verify OTP for phone recovery", description = "Validates the OTP sent to the user's registered phone number during forgot password or MPIN recovery.")
	@PostMapping("/forgot/verify-phone")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyForgotPhone(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody OtpVerifyRequest request) {

		log.info("[PublicController /forgot/verify-phone] OTP verification request received for forgot password/MPIN");

		ApiResponse<Map<String, String>> response = userService.verifyForgotPhone(authorizationHeader, request);

		log.info("[PublicController /forgot/verify-phone] OTP verified successfully for phone");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Verifies the OTP for password recovery and resets the user's password.
	 */
	@Operation(summary = "Verify OTP and reset password", description = "Validates the OTP sent for password recovery and updates the user's password upon successful verification.")
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody PasswordRequest request) {

		log.info("[PublicController /reset-password] Password reset request received");

		ApiResponse<Map<String, String>> response = userService.resetPassword(authorizationHeader, request);

		log.info("[PublicController /reset-password] Password reset completed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Verifies the OTP for MPIN recovery and resets the user's MPIN.
	 */
	@Operation(summary = "Verify OTP and reset MPIN", description = "Validates the OTP sent for MPIN recovery and updates the user's MPIN upon successful verification.")
	@PostMapping("/reset-mpin")
	public ResponseEntity<ApiResponse<Map<String, String>>> resetMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody MpinRequest request) {

		log.info("[PublicController /reset-mpin] MPIN reset request received");

		ApiResponse<Map<String, String>> response = userService.resetMpin(authorizationHeader, request);

		log.info("[PublicController /reset-mpin] MPIN reset completed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Re-sends an OTP if the user did not receive it previously.
	 */
	@Operation(summary = "Re-send OTP", description = "Sends a new OTP to the user's registered phone number or email if the previous OTP was not received.")
	@PostMapping("/re-send-otp")
	public ResponseEntity<ApiResponse<Map<String, String>>> reSendOtp(@Valid @RequestBody ReSendOtpRequest request) {

		log.info("[PublicController /re-send-otp] Re-send OTP request received");

		ApiResponse<Map<String, String>> response = userService.reSendOtp(request);

		log.info("[PublicController /re-send-otp] Re-send OTP request completed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Changes the user's password using the existing password. No OTP is required
	 * for this operation.
	 */
	@Operation(summary = "Change password", description = "Allows a logged-in user to update their password by providing the current password. OTP verification is not required.")
	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse<Void>> changePassword(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody ChangePasswordRequest request) {

		log.info("[PublicController /change-password] Change password request received");

		ApiResponse<Void> response = userService.changePassword(authorizationHeader, request);

		log.info("[PublicController /change-password] Change password request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Changes the user's MPIN using the existing MPIN. No OTP is required for this
	 * operation.
	 */
	@Operation(summary = "Change MPIN", description = "Allows a logged-in user to update their MPIN by providing the current MPIN. OTP verification is not required.")
	@PostMapping("/change-mpin")
	public ResponseEntity<ApiResponse<Void>> changeMpin(
			@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody ChangeMpinRequest request) {

		log.info("[PublicController /change-mpin] Change MPIN request received");

		ApiResponse<Void> response = userService.changeMpin(authorizationHeader, request);

		log.info("[PublicController /change-mpin] Change MPIN request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Recover the user ID using a registered email or phone number.
	 */
	@Operation(summary = "Recover user ID", description = "Allows a user to retrieve their user ID by providing a registered email address or phone number.")
	@PostMapping("/remember-userid")
	public ResponseEntity<ApiResponse<Void>> rememberUserId(@Valid @RequestBody RememberUserNameRequest request) {

		log.info("[PublicController /remember-userid] Recover user ID request received");

		ApiResponse<Void> response = userService.rememberUserId(request);

		log.info("[PublicController /remember-userid] Recover user ID request processed successfully");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
//END