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

	String login(LoginRequest loginRequest);

	String register(RegisterRequest registerRequest);

	String forgotPassword(ForgotPasswordByOtpRequest forgotPasswordByOtpRequest);

	String resetPassword(ResetPasswordByOtpRequest resetPasswordByOtpRequest);

	String verifyUser(VerifyRequest verifyRequest);

	String changePassword(ChangePasswordRequest changePasswordRequest);

	String otpResend(ReSendOtpRequest reSendOtpRequest);
	
	String rememberUserId(RememberUserIdRequest rememberUserIdRequest);
	
	String forgotPasswordBySecurityQuestion(ForgotPasswordBySecurityQuestionRequest forgotPasswordBySecurityQuestionRequest);

	String resetPasswordBySecurityQuestion(ResetPasswordBySecurityQuestionRequest resetPasswordBySecurityQuestionRequest);
}