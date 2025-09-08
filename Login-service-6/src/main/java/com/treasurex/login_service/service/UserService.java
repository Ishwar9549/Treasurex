package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.VerifyRequest;

public interface UserService {

	String login(LoginRequest loginRequest);

	String register(RegisterRequest registerRequest);

	String forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

	String resetPassword(ResetPasswordRequest resetPasswordRequest);

	String verifyUser(VerifyRequest verifyRequest);

	String changePassword(ChangePasswordRequest changePasswordRequest);

	String otpResend(ReSendOtpRequest reSendOtpRequest);
}