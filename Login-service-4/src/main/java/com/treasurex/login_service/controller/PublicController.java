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
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserLoginRequest;
import com.treasurex.login_service.dto.UserRegisterRequest;
import com.treasurex.login_service.dto.VerifyUserRequest;
import com.treasurex.login_service.service.UserService;

@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

	private final UserService userService;

	@GetMapping("/test")
	public ResponseEntity<Map<String, String>> test() {
		log.info("Public Controller test endpoint is reached");
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "public test is reached"));
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> userRegister(
			@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
		log.info("Public Controller register endpoint called for email: {}", userRegisterRequest.getEmail());
		String registeredUser = userService.register(userRegisterRequest);
		log.info("Public Controller user registered successfully: {}");
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", registeredUser));
	}

	@PostMapping("/verify-user")
	public ResponseEntity<Map<String, String>> verifyUser(@Valid @RequestBody VerifyUserRequest verifyUserRequest) {
		log.info("Public Controller verify-user endpoint called for email: {}", verifyUserRequest.getEmail());
		String result = userService.verifyUser(verifyUserRequest);
		log.info("Public Controller user verified successfully: {}");
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> userLogin(@Valid @RequestBody UserLoginRequest userLoginRequest) {
		log.info("Public Controller Login endpoint called for email: {}", userLoginRequest.getEmail());
		String token = userService.login(userLoginRequest);
		log.info("Public Controller Login successful for email: {}", userLoginRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("Token", token));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<Map<String, String>> forgotPassword(
			@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		log.info("Public Controller Forgot password endpoint called for email: {}", forgotPasswordRequest.getEmail());
		String result = userService.forgotPassword(forgotPasswordRequest.getEmail());
		log.info("Public Controller Forgot password OTP sent to mail");
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Map<String, String>> resetPassword(
			@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.info("Public Controller Reset password endpoint called for email: {}", resetPasswordRequest.getEmail());
		String result = userService.resetPassword(resetPasswordRequest.getCode(), resetPasswordRequest.getEmail(),
				resetPasswordRequest.getNewPassword());
		log.info("Public Controller Password updated successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}

	@PostMapping("/change-password")
	public ResponseEntity<Map<String, String>> changePassword(
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		log.info("Public Controller change Password endpoint called");
		String result = userService.changePassword(changePasswordRequest);
		log.info("Public Controller password changed successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}
}