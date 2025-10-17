package com.treasurex.login_service.service;

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

public interface UserService {

	String register(RegisterRequest registerRequest);

	String verifyUserByOtp(VerifyRequest verifyRequest);
	
	String otpResend(ReSendOtpRequest reSendOtpRequest);
	
	String login(LoginRequest loginRequest);
	
	String forgotPasswordByOtp(ForgotPasswordByOtpRequest forgotPasswordByOtpRequest);

	String resetPasswordByOtp(ResetPasswordByOtpRequest resetPasswordByOtpRequest);

	String changePassword(ChangePasswordRequest changePasswordRequest);

	String rememberUserId(RememberUserIdRequest rememberUserIdRequest);

	String forgotPasswordBySecurityQuestion(
			ForgotPasswordBySecurityQuestionRequest forgotPasswordBySecurityQuestionRequest);

	String resetPasswordBySecurityQuestion(
			ResetPasswordBySecurityQuestionRequest resetPasswordBySecurityQuestionRequest);
}