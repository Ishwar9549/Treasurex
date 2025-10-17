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

	String register(RegisterRequest registerRequest);

	String verifyUserByOtp(UserVerifyRequest userVerifyRequest);

	String login(LoginRequest loginRequest);

	String getSecurityQuestion(ForgotPasswordRequest forgotPasswordRequest);

	String verifySecurityQuestionAndSendOtp(VerifySecurityAnswerRequest verifySecurityAnswerRequest);

	String resetPasswordByOtp(ResetPasswordRequest resetPasswordRequest);

	String otpResend(ReSendOtpRequest reSendOtpRequest);

	String rememberUserId(RememberUserIdRequest rememberUserIdRequest);

	String changePassword(ChangePasswordRequest changePasswordRequest);
}