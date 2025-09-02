package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserLoginRequest;
import com.treasurex.login_service.dto.UserRegisterRequest;
import com.treasurex.login_service.dto.VerifyUserRequest;

public interface UserService {

	String login(UserLoginRequest userLoginRequest);

	String register(UserRegisterRequest userRegisterRequest);

	String forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

	String resetPassword(ResetPasswordRequest resetPasswordRequest);

	String verifyUser(VerifyUserRequest verifyUserRequest);

	String changePassword(ChangePasswordRequest changePasswordRequest);

	String otpResend(ReSendOtpRequest reSendOtpRequest);
}