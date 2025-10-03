package com.treasurex.login_service.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;
import com.treasurex.login_service.exception.InvalidCredentialsException;
import com.treasurex.login_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Public API", description = "User Registration, Login, Forgot password, Change password")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

	private final UserService userService;

	/*
	 * Test end point to check if controller is reachable
	 */
	@Operation(summary = "Public Controller Test end point to check if controller is reachable")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse> test() {
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("public test is reached").build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	/*
	 * Start user registration with basic credentials.
	 */
	@Operation(summary = "Start user registration with basic credentials", description = "Registers user with email, phone, userId, password")
	@PostMapping("/register/start")
	public ResponseEntity<ApiResponse> userRegisterStart(
			@Valid @RequestBody RegisterStartRequest registerStartRequest) {
		if (!registerStartRequest.getPassword().equals(registerStartRequest.getConfirmPassword())) {
			throw new InvalidCredentialsException("Password and Confirm Password do not match");
		}
		String result = userService.registerStart(registerStartRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).nextStep("Verify-user")
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	/*
	 * Verify newly registered user with OTP (first-time account activation)
	 */
	@Operation(summary = "Verify newly registered user with OTP (first-time account activation)", description = "")
	@PostMapping("/verify-user")
	public ResponseEntity<ApiResponse> verifyUser(@Valid @RequestBody UserVerifyRequest userVerifyRequest) {
		String result = userService.verifyUserByOtp(userVerifyRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).nextStep("Personal_INFO")
				.build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	/*
	 * Handles the next step of user registration provide personal information.
	 */
	@Operation(summary = "Handles the next step of user registration provide personal information.", description = "")
	@PostMapping("/register/personal")
	public ResponseEntity<ApiResponse> userRegisterPersonal(
			@Valid @RequestBody RegisterPersonalRequest registerPersonalRequest) {
		String result = userService.registerPersonal(registerPersonalRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).nextStep("Address_INFO")
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	/*
	 * Handles the next step of user registration provide address information.
	 */
	@Operation(summary = "Handles the next step of user registration provide address information.", description = "")
	@PostMapping("/register/address")
	public ResponseEntity<ApiResponse> userRegisterAddress(
			@Valid @RequestBody RegisterAddressRequest registerAddressRequest) {
		String result = userService.registerAddress(registerAddressRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result)
				.nextStep("Security_details_INFO").build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	/*
	 * Handles the next step of user registration provide security information.
	 */
	@Operation(summary = "Handles the next step of user registration provide security information.", description = "")
	@PostMapping("/register/security_questions")
	public ResponseEntity<ApiResponse> userRegisterSecurityQuestions(
			@Valid @RequestBody RegisterSecurityQuestionsRequest registerSecurityQuestionsRequest) {
		String result = userService.registerSecurityQuestions(registerSecurityQuestionsRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	/*
	 * Login with userId /Email/Phone number and password(returns JWT token if
	 * valid)
	 */
	@Operation(summary = "Login with userId /Email/Phone number and password(returns JWT token if valid", description = "")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
		String token = userService.login(loginRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").token(token)
				.message("Login successful. Use this token for authenticated requests.").build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	/*
	 * Forgot password → get user’s random security question
	 */
	@Operation(summary = "Forgot password → get user’s random security question", description = "")
	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		String question = userService.getSecurityQuestion(forgotPasswordRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("FIRST_LEVEL_SECURITY_CHECK")
				.message("Answer the security question to proceed")
				.nextStep("Submit your answer at /public/verify-security-question "
						+ " and you will receive OTP in mail for second-level security check")
				.data(Map.of("securityQuestion", question)).build();
		return ResponseEntity.ok(apiResponse);
	}

	/*
	 * Verify security question answer → if correct, send OTP to email
	 */
	@Operation(summary = "Verify security question answer → if correct, send OTP to email", description = "")
	@PostMapping("/verify-security-question")
	public ResponseEntity<ApiResponse> verifySecurityQuestion(
			@Valid @RequestBody VerifySecurityAnswerRequest verifySecurityAnswerRequest) {
		String result = userService.verifySecurityQuestionAndSendOtp(verifySecurityAnswerRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SECOND_LEVEL_SECURITY_CHECK").message(result)
				.nextStep("Submit your otp with new password at public/reset-password").build();
		return ResponseEntity.ok(apiResponse);
	}

	/*
	 * Verify OTP and reset the password
	 */
	@Operation(summary = "Verify OTP and reset the password", description = "")
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		String result = userService.resetPasswordByOtp(resetPasswordRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	/*
	 * Re-send OTP if user didn’t receive it earlier for Account verification/Reset
	 * password
	 */
	@Operation(summary = "Re-send OTP if user didn’t receive it earlier for Account verification/Reset password", description = "")
	@PostMapping("/re-send-otp")
	public ResponseEntity<ApiResponse> reSendOtp(@Valid @RequestBody ReSendOtpRequest reSendOtpRequest) {
		String result = userService.otpResend(reSendOtpRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	/*
	 * Remember userId by providing registered email / phone number
	 */
	@Operation(summary = "Remember userId by providing registered email / phone number", description = "")
	@PostMapping("/remember-UserId")
	public ResponseEntity<ApiResponse> rememberUserId(@Valid @RequestBody RememberUserIdRequest rememberUserIdRequest) {
		String result = userService.rememberUserId(rememberUserIdRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	/*
	 * Change password (if user knows old password → no OTP/security required)
	 */
	@Operation(summary = "Change password (if user knows old password → no OTP/security required)", description = "")
	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
			throw new InvalidCredentialsException("New Password and Confirm Password do not match");
		}
		String result = userService.changePassword(changePasswordRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}
}
