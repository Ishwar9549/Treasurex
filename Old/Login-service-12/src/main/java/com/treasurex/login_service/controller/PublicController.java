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

	@GetMapping("/test")
	public ResponseEntity<Map<String, String>> test() {
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "public test is reached"));
	}

	// register user
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> userRegister(@Valid @RequestBody RegisterRequest registerRequest) {
		String result = userService.register(registerRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	// verify user with OTP for the 1st time
	@PostMapping("/verify-user")
	public ResponseEntity<Map<String, String>> verifyUser(@Valid @RequestBody UserVerifyRequest userVerifyRequest) {
		String result = userService.verifyUserByOtp(userVerifyRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	// login
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
		String token = userService.login(loginRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("Token", token));
	}

	// Get security question
	@PostMapping("/forgot-password")
	public ResponseEntity<Map<String, String>> forgotPassword(
			@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		String question = userService.getSecurityQuestion(forgotPasswordRequest);
		return ResponseEntity.ok(Map.of("status", "FIRST_LEVEL_SECURITY_CHECK", "message",
				"Answer the security question to proceed", "securityQuestion", question, "nextStep",
				"Submit your answer at /public/verify-security-question "
						+ " and you will recive otp in mail for Second level Security check"));
	}

	// Verify security question by answer and send OTP
	@PostMapping("/verify-security-question")
	public ResponseEntity<Map<String, String>> verifySecurityQuestion(
			@Valid @RequestBody VerifySecurityAnswerRequest verifySecurityAnswerRequest) {
		String result = userService.verifySecurityQuestionAndSendOtp(verifySecurityAnswerRequest);
		return ResponseEntity.ok(Map.of("status", "SECOND_LEVEL_SECURITY_CHECK", "message", result, "nextStep",
				"Submit OTP with new password at public/reset-password"));
	}

	// Verify OTP and reset password
	@PostMapping("/reset-password")
	public ResponseEntity<Map<String, String>> resetPassword(
			@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		String result = userService.resetPasswordByOtp(resetPasswordRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	// re-send OTP
	@PostMapping("/re-send-otp")
	public ResponseEntity<Map<String, String>> reSendOtp(@Valid @RequestBody ReSendOtpRequest reSendOtpRequest) {
		String result = userService.otpResend(reSendOtpRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	// remember user id
	@PostMapping("/remember-UserId")
	public ResponseEntity<Map<String, String>> rememberUserId(
			@Valid @RequestBody RememberUserIdRequest rememberUserIdRequest) {
		String result = userService.rememberUserId(rememberUserIdRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("User ID : ", result));
	}

	// change password if you know old password no need OTP verification
	@PostMapping("/change-password")
	public ResponseEntity<Map<String, String>> changePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		String result = userService.changePassword(changePasswordRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}
}
