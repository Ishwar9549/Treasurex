package com.treasurex.login_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
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
		log.info("PublicController: Verify-user endpoint called for email: {}", verifyRequest.getEmail());
		String result = userService.verifyUser(verifyRequest);
		log.info("PublicController: User verified successfully for email: {}", verifyRequest.getEmail());
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
	public ResponseEntity<Map<String, String>> forgotPassword(
			@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		log.info("PublicController: Forgot-password endpoint called for email: {}", forgotPasswordRequest.getEmail());
		String result = userService.forgotPassword(forgotPasswordRequest);
		log.info("PublicController: OTP for password reset sent successfully to email: {}",
				forgotPasswordRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Map<String, String>> resetPassword(
			@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.info("PublicController: Reset-password endpoint called for email: {}", resetPasswordRequest.getEmail());
		String result = userService.resetPassword(resetPasswordRequest);
		log.info("PublicController: Password reset successfully for email: {}", resetPasswordRequest.getEmail());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	@PostMapping("/change-password")
	public ResponseEntity<Map<String, String>> changePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		log.info("PublicController: Change-password endpoint called for email: {}", changePasswordRequest.getEmail());
		String result = userService.changePassword(changePasswordRequest);
		log.info("PublicController: Password changed successfully for email: {}", changePasswordRequest.getEmail());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	@PostMapping("/re-send-otp")
	public ResponseEntity<String> reSendOtp(@Valid @RequestBody ReSendOtpRequest reSendOtpRequest) {
		log.info("PublicController: Resend-OTP endpoint called for email: {}", reSendOtpRequest.getEmail());
		String result = userService.otpResend(reSendOtpRequest);
		log.info("PublicController: OTP resent successfully to email: {}", reSendOtpRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}