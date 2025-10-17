package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;

public interface UserService {

	// Registers a new user and sends OTP for verification.
	String register(RegisterRequest registerRequest);

	// Verifies a newly registered user with OTP.
	String verifyUserByOtp(UserVerifyRequest userVerifyRequest);

	// Logs in a user after verifying credentials and returns JWT token.
	String login(LoginRequest loginRequest);

	// Retrieves a random security question for a given user.
	String getSecurityQuestion(ForgotPasswordRequest forgotPasswordRequest);

	// Verifies the user’s security answer and sends OTP if correct.
	String verifySecurityQuestionAndSendOtp(VerifySecurityAnswerRequest verifySecurityAnswerRequest);

	// Resets password after validating OTP.
	String resetPasswordByOtp(ResetPasswordRequest resetPasswordRequest);

	// Resends OTP for either account verification or password reset.
	String otpResend(ReSendOtpRequest reSendOtpRequest);

	// Retrieves the user ID associated with a given email.
	String rememberUserId(RememberUserIdRequest rememberUserIdRequest);

	// Changes password if old password is correct(no Security/OTP required)
	String changePassword(ChangePasswordRequest changePasswordRequest);
}