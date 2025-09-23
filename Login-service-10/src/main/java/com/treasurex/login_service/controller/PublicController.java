package com.treasurex.login_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.treasurex.login_service.dto.ForgotPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordBySecurityQuestionRequest;
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

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
		log.info("PublicController: Login endpoint called for email: {}", loginRequest.getUserId());
		String token = userService.login(loginRequest);
		log.info("PublicController: Login successful for email: {}", loginRequest.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("Token", token));
	}

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
			@Valid @RequestBody ResetPasswordBySecurityQuestionRequest requestPasswordBySecurityQuestionRequest) {
		log.info("PublicController: [resetPasswordBySecurityQuestion] endpoint called for email: {}",
				requestPasswordBySecurityQuestionRequest.getUserId());

		String result = userService.resetPasswordBySecurityQuestion(requestPasswordBySecurityQuestionRequest);

		log.info("PublicController: [resetPasswordBySecurityQuestion] password reset successfully for email: {}",
				requestPasswordBySecurityQuestionRequest.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", result));
	}
}
