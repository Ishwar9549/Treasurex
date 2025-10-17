package com.treasurex.login_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordByOtpRequest;
import com.treasurex.login_service.dto.ForgotPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordByOtpRequest;
import com.treasurex.login_service.dto.ResetPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.VerifyRequest;
import com.treasurex.login_service.service.UserService;

@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

	private final UserService userService;

	@GetMapping("/test")
	public ResponseEntity<Map<String, String>> test() {
		log.info("PublicController: Test endpoint reached");
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "public test is reached"));
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> userRegister(@Valid @RequestBody RegisterRequest registerRequest) {
		log.info("PublicController: Register endpoint called for email: {}", registerRequest.getUserId());
		String result = userService.register(registerRequest);
		log.info("PublicController: User registered successfully for email: {}", registerRequest.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	@PostMapping("/verify-user")
	public ResponseEntity<Map<String, String>> verifyUser(@Valid @RequestBody VerifyRequest verifyRequest) {
		log.info("PublicController: Verify-user endpoint called for email: {}", verifyRequest.getUserId());
		String result = userService.verifyUserByOtp(verifyRequest);
		log.info("PublicController: User verified successfully for email: {}", verifyRequest.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
		log.info("PublicController: Login endpoint called for email: {}", loginRequest.getUserId());
		String token = userService.login(loginRequest);
		log.info("PublicController: Login successful for email: {}", loginRequest.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("Token", token));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<Map<String, String>> forgotPasswordByOtp(
			@Valid @RequestBody ForgotPasswordByOtpRequest forgotPasswordByOtpRequest) {
		log.info("PublicController: Forgot-password endpoint called for user Id: {}",
				forgotPasswordByOtpRequest.getUserId());
		String result = userService.forgotPasswordByOtp(forgotPasswordByOtpRequest);
		log.info("PublicController: OTP for password reset sent successfully to user Id: {}",
				forgotPasswordByOtpRequest.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Map<String, String>> resetPasswordByOtp(
			@Valid @RequestBody ResetPasswordByOtpRequest resetPasswordByOtpRequest) {
		log.info("PublicController: Reset-password endpoint called for user Id: {}",
				resetPasswordByOtpRequest.getUserId());
		String result = userService.resetPasswordByOtp(resetPasswordByOtpRequest);
		log.info("PublicController: Password reset successfully for user Id: {}", resetPasswordByOtpRequest.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	@PostMapping("/change-password")
	public ResponseEntity<Map<String, String>> changePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		log.info("PublicController: Change-password endpoint called for user Id: {}", changePasswordRequest.getUserId());
		String result = userService.changePassword(changePasswordRequest);
		log.info("PublicController: Password changed successfully for user Id: {}", changePasswordRequest.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	@PostMapping("/re-send-otp")
	public ResponseEntity<Map<String, String>> reSendOtp(@Valid @RequestBody ReSendOtpRequest reSendOtpRequest) {
		log.info("PublicController: Resend-OTP endpoint called for user Id: {}", reSendOtpRequest.getUserId());
		String result = userService.otpResend(reSendOtpRequest);
		log.info("PublicController: OTP resent successfully to user Id: {}", reSendOtpRequest.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

//========================================================  changes  ====================================
	@PostMapping("/remember-UserId")
	public ResponseEntity<Map<String, String>> rememberUserId(
			@Valid @RequestBody RememberUserIdRequest rememberUserIdRequest) {
		log.info("PublicController: [rememberUserId] endpoint called for email: {}", rememberUserIdRequest.getEmail());
		String result = userService.rememberUserId(rememberUserIdRequest);
		log.info("PublicController: [rememberUserId] successfully processed for email: {}",
				rememberUserIdRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("User ID : ", result));
	}

	@PostMapping("/forgot-password-by-security-question")
	public ResponseEntity<Map<String, String>> forgotPasswordBySecurityQuestion(
			@Valid @RequestBody ForgotPasswordBySecurityQuestionRequest forgotPasswordBySecurityQuestionRequest) {
		log.info("PublicController: [forgotPasswordBySecurityQuestion] endpoint called for USER ID: {}",
				forgotPasswordBySecurityQuestionRequest.getUserId());

		String result = userService.forgotPasswordBySecurityQuestion(forgotPasswordBySecurityQuestionRequest);

		log.info(
				"PublicController: [forgotPasswordBySecurityQuestion] security question fetched successfully for USER ID: {}",
				forgotPasswordBySecurityQuestionRequest.getUserId());
		return ResponseEntity.status(HttpStatus.OK)
				.body(Map.of("Question", result, "next step", "ans for this Question and reset new password"));
	}

	@PostMapping("/reset-password-by-security-question")
	public ResponseEntity<Map<String, String>> resetPasswordBySecurityQuestion(
			@Valid @RequestBody ResetPasswordBySecurityQuestionRequest resetPasswordBySecurityQuestionRequest) {
		log.info("PublicController: [resetPasswordBySecurityQuestion] endpoint called for email: {}",
				resetPasswordBySecurityQuestionRequest.getUserId());

		String result = userService.resetPasswordBySecurityQuestion(resetPasswordBySecurityQuestionRequest);

		log.info("PublicController: [resetPasswordBySecurityQuestion] password reset successfully for email: {}",
				resetPasswordBySecurityQuestionRequest.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}
}
