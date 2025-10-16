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
import com.treasurex.login_service.dto.RegistrationVerifyResponse;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserIdCheckRequest;
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;
import com.treasurex.login_service.exception.InvalidCredentialsException;
import com.treasurex.login_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Public API", description = "User Registration, Login, Forgot Password, Change Password")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

	private final UserService userService;

	// ---------------- Test End point ----------------
	@Operation(summary = "Public Controller Test endpoint to check if controller is reachable")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse> test() {
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Public test is reached").build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Start Registration ----------------
	@Operation(summary = "Start user registration with basic credentials", description = "Registers user with email, phone, userId, password")
	@PostMapping("/register/start")
	public ResponseEntity<ApiResponse> userRegisterStart(
			@Valid @RequestBody RegisterStartRequest request) {

		// Check if userId is available
		boolean isUserIdAvailable = userService
				.isUserIdAvailable(UserIdCheckRequest.builder().userId(request.getUserId()).build());

		if (!isUserIdAvailable) {
			ApiResponse apiResponse = ApiResponse.builder().status("USER_ID_TAKEN")
					.message("The userId `" + request.getUserId() + "` is already taken").build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		String result = userService.registerStart(request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).nextStep("VERIFY_USER")
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	// ---------------- Verify User (OTP) ----------------
	@Operation(summary = "Verify newly registered user with OTP (first-time account activation)")
	@PostMapping("/verify-user")
	public ResponseEntity<ApiResponse> verifyUser(@Valid @RequestBody UserVerifyRequest request) {
		RegistrationVerifyResponse resp = userService.verifyUserByOtp(request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(resp.getMessage())
				.token(resp.getRegistrationToken()) // short-lived registration token
				.nextStep("PERSONAL_INFO").build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Register Personal Info ----------------
	@Operation(summary = "Provide personal information for registration")
	@PostMapping("/register/personal")
	public ResponseEntity<ApiResponse> userRegisterPersonal(@RequestHeader(name = "Authorization") String authorization,
			@Valid @RequestBody RegisterPersonalRequest request) {

		String result = userService.registerPersonalWithToken(authorization, request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).nextStep("ADDRESS_INFO")
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	// ---------------- Register Address Info ----------------
	@Operation(summary = "Provide address information for registration")
	@PostMapping("/register/address")
	public ResponseEntity<ApiResponse> userRegisterAddress(@RequestHeader(name = "Authorization") String authorization,
			@Valid @RequestBody RegisterAddressRequest request) {

		String result = userService.registerAddressWithToken(authorization, request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result)
				.nextStep("SECURITY_QUESTIONS_INFO").build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	// ---------------- Register Security Questions ----------------
	@Operation(summary = "Provide security questions for registration")
	@PostMapping("/register/security_questions")
	public ResponseEntity<ApiResponse> userRegisterSecurityQuestions(
			@RequestHeader(name = "Authorization") String authorization,
			@Valid @RequestBody RegisterSecurityQuestionsRequest request) {

		String result = userService.registerSecurityQuestionsWithToken(authorization, request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	// ---------------- User Login ----------------
	@Operation(summary = "Login with userId / Email / Phone number (returns JWT token if valid)")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
		String token = userService.login(loginRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").token(token)
				.message("Login successful. Use this token for authenticated requests.").build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Forgot Password ----------------
	@Operation(summary = "Forgot password → get user’s random security question")
	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
		String question = userService.getSecurityQuestion(forgotPasswordRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("FIRST_LEVEL_SECURITY_CHECK")
				.message("Answer the security question to proceed")
				.nextStep(
						"Submit your answer at /public/verify-security-question and receive OTP for second-level security check")
				.data(Map.of("securityQuestion", question)).build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Verify Security Question ----------------
	@Operation(summary = "Verify security question answer → if correct, send OTP to email")
	@PostMapping("/verify-security-question")
	public ResponseEntity<ApiResponse> verifySecurityQuestion(
			@Valid @RequestBody VerifySecurityAnswerRequest verifySecurityAnswerRequest) {

		String result = userService.verifySecurityQuestionAndSendOtp(verifySecurityAnswerRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SECOND_LEVEL_SECURITY_CHECK").message(result)
				.nextStep("Submit OTP with new password at /public/reset-password").build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Reset Password ----------------
	@Operation(summary = "Verify OTP and reset the password")
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		String result = userService.resetPasswordByOtp(resetPasswordRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Re-send OTP ----------------
	@Operation(summary = "Re-send OTP for Account verification / Reset password")
	@PostMapping("/re-send-otp")
	public ResponseEntity<ApiResponse> reSendOtp(@Valid @RequestBody ReSendOtpRequest reSendOtpRequest) {
		String result = userService.otpResend(reSendOtpRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Remember UserId ----------------
	@Operation(summary = "Remember userId by providing registered email / phone number")
	@PostMapping("/remember-UserId")
	public ResponseEntity<ApiResponse> rememberUserId(@Valid @RequestBody RememberUserIdRequest rememberUserIdRequest) {
		String result = userService.rememberUserId(rememberUserIdRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- Change Password ----------------
	@Operation(summary = "Change password (if user knows old password → no OTP/security required)")
	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
		if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
			throw new InvalidCredentialsException("New Password and Confirm Password do not match");
		}
		String result = userService.changePassword(changePasswordRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.ok(apiResponse);
	}

	// --------------------new change implements userId suggestion feature
	@Operation(summary = "Check if User ID is available", description = "Validates whether a userId is already taken or available for registration")
	@PostMapping("/check-userId")
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	public ResponseEntity<ApiResponse> checkUserIdAvailability(@Valid @RequestBody UserIdCheckRequest request) {
		boolean isAvailable = userService.isUserIdAvailable(request);
		String message = isAvailable ? "User ID is available" : "User ID is already taken";

		ApiResponse apiResponse = ApiResponse.builder().status(isAvailable ? "AVAILABLE" : "TAKEN").message(message)
				.data(Map.of("userId", request.getUserId(), "available", isAvailable)).build();

		return ResponseEntity.ok(apiResponse);
	}
}
