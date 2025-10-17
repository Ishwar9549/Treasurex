package com.treasurex.login_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;
import com.treasurex.login_service.service.UserService;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

	private final UserService userService;

	// Test end point to check if controller is reachable
	@GetMapping("/test")
	public ResponseEntity<Map<String, String>> test() {
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "public test is reached"));
	}

	// Register a new user (creates user record and sends OTP for verification)
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> userRegister(@Valid @RequestBody RegisterRequest registerRequest) {
		String result = userService.register(registerRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	// Verify newly registered user with OTP (first-time account activation)
	@PostMapping("/verify-user")
	public ResponseEntity<Map<String, String>> verifyUser(@Valid @RequestBody UserVerifyRequest userVerifyRequest) {
		String result = userService.verifyUserByOtp(userVerifyRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	// Login with userId and password (returns JWT token if valid)
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
		String token = userService.login(loginRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("Token", token));
	}

	// Forgot password → get user’s random security question
	@PostMapping("/forgot-password")
	public ResponseEntity<Map<String, String>> forgotPassword(
			@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		String question = userService.getSecurityQuestion(forgotPasswordRequest);
		return ResponseEntity.ok(Map.of("status", "FIRST_LEVEL_SECURITY_CHECK", "message",
				"Answer the security question to proceed", "securityQuestion", question, "nextStep",
				"Submit your answer at /public/verify-security-question "
						+ " and you will recive otp in mail for Second level Security check"));
	}

	// Verify security question answer → if correct, send OTP to email
	@PostMapping("/verify-security-question")
	public ResponseEntity<Map<String, String>> verifySecurityQuestion(
			@Valid @RequestBody VerifySecurityAnswerRequest verifySecurityAnswerRequest) {
		String result = userService.verifySecurityQuestionAndSendOtp(verifySecurityAnswerRequest);
		return ResponseEntity.ok(Map.of("status", "SECOND_LEVEL_SECURITY_CHECK", "message", result, "nextStep",
				"Submit OTP with new password at public/reset-password"));
	}

	// Verify OTP and reset the password
	@PostMapping("/reset-password")
	public ResponseEntity<Map<String, String>> resetPassword(
			@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		String result = userService.resetPasswordByOtp(resetPasswordRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	// Re-send OTP if user didn’t receive it earlier
	@PostMapping("/re-send-otp")
	public ResponseEntity<Map<String, String>> reSendOtp(@Valid @RequestBody ReSendOtpRequest reSendOtpRequest) {
		String result = userService.otpResend(reSendOtpRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	// Remember userId by providing registered email
	@PostMapping("/remember-UserId")
	public ResponseEntity<Map<String, String>> rememberUserId(
			@Valid @RequestBody RememberUserIdRequest rememberUserIdRequest) {
		String result = userService.rememberUserId(rememberUserIdRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("User ID : ", result));
	}

	// Change password (if user knows old password → no OTP/security required)
	@PostMapping("/change-password")
	public ResponseEntity<Map<String, String>> changePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		String result = userService.changePassword(changePasswordRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}
}
