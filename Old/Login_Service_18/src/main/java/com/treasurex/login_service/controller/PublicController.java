package com.treasurex.login_service.controller;

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
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;
import com.treasurex.login_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User_service, Public API", description = "User Registration, Login, Forgot Password, Change Password")
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
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Public test is reached"));
	}

	/*
	 * Start user registration with basic credentials.
	 */
	@Operation(summary = "Start user registration with basic credentials", description = "Registers user with email, phone, userId, password")
	@PostMapping("/register/start")
	public ResponseEntity<ApiResponse<Map<String, String>>> userRegisterStart(
			@Valid @RequestBody RegisterStartRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerStart(request));
	}

	/*
	 * Verify newly registered user with OTP (first-time account activation)
	 */
	@Operation(summary = "Verify newly registered user with OTP (first-time account activation)")
	@PostMapping("/verify-user")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifyUser(@Valid @RequestBody UserVerifyRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.verifyUserByOtp(request));
	}

	/*
	 * Handles the next step of user registration provide/Register personal
	 * information.
	 */
	@Operation(summary = "Provide personal information for registration")
	@PostMapping("/register/personal")
	public ResponseEntity<ApiResponse<Map<String, String>>> userRegisterPersonal(
			@RequestHeader(name = "Authorization") String authorization,
			@Valid @RequestBody RegisterPersonalRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(userService.registerPersonalWithToken(authorization, request));
	}

	/*
	 * Handles the next step of user registration provide/Register address
	 * information.
	 */
	@Operation(summary = "Provide address information for registration")
	@PostMapping("/register/address")
	public ResponseEntity<ApiResponse<Map<String, String>>> userRegisterAddress(
			@RequestHeader(name = "Authorization") String authorization,
			@Valid @RequestBody RegisterAddressRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(userService.registerAddressWithToken(authorization, request));
	}

	/*
	 * Handles the next step of user registration provide/Register security
	 * information.
	 */
	@Operation(summary = "Provide security questions for registration")
	@PostMapping("/register/security_questions")
	public ResponseEntity<ApiResponse<Void>> userRegisterSecurityQuestions(
			@RequestHeader(name = "Authorization") String authorization,
			@Valid @RequestBody RegisterSecurityQuestionsRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(userService.registerSecurityQuestionsWithToken(authorization, request));
	}

	/*
	 * Login with userId /Email/Phone number and password(returns JWT token if
	 * valid)
	 */
	@Operation(summary = "Login with userId / Email / Phone number (returns JWT token if valid)")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, String>>> userLogin(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.login(request));
	}

	/*
	 * Forgot password → get user’s random security question
	 */
	@Operation(summary = "Forgot password → get user’s random security question")
	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
			@Valid @RequestBody ForgotPasswordRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.getSecurityQuestion(request));
	}

	/*
	 * Verify security question answer → if correct, send OTP to email
	 */
	@Operation(summary = "Verify security question answer → if correct, send OTP to email")
	@PostMapping("/verify-security-question")
	public ResponseEntity<ApiResponse<Map<String, String>>> verifySecurityQuestion(
			@Valid @RequestBody VerifySecurityAnswerRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.verifySecurityQuestionAndSendOtp(request));
	}

	/*
	 * Verify OTP and reset the password
	 */
	@Operation(summary = "Verify OTP and reset the password")
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.resetPasswordByOtp(request));
	}

	/*
	 * Re-send OTP if user didn’t receive it earlier for Account verification/Reset
	 * password
	 */
	@Operation(summary = "Re-send OTP for Account verification / Reset password")
	@PostMapping("/re-send-otp")
	public ResponseEntity<ApiResponse<Void>> reSendOtp(@Valid @RequestBody ReSendOtpRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.otpResend(request));
	}

	/*
	 * Change password (if user knows old password → no OTP/security required)
	 */
	@Operation(summary = "Change password (if user knows old password → no OTP/security required)")
	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse<Void>> changePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.changePassword(changePasswordRequest));
	}

	/*
	 * Remember userId by providing registered email / phone number
	 */
	@Operation(summary = "Remember userId by providing registered email / phone number")
	@PostMapping("/remember-UserId")
	public ResponseEntity<ApiResponse<Void>> rememberUserId(
			@Valid @RequestBody RememberUserIdRequest rememberUserIdRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.rememberUserId(rememberUserIdRequest));
	}

	/**
	 * userId suggestion feature
	 * 
	 */
	@Operation(summary = "Check if User ID is available", description = "Validates whether a userId is already taken or available for registration")
	@PostMapping("/check-userId")
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	public ResponseEntity<ApiResponse<Void>> checkUserIdAvailability(@Valid @RequestBody UserIdCheckRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.isUserIdAvailable(request));
	}
}
//END