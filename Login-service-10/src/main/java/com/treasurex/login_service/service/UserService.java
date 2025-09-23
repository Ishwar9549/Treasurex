package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.ForgotPasswordBySecurityQuestionRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.RegisterRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordBySecurityQuestionRequest;

public interface UserService {

	String login(LoginRequest loginRequest);

	String register(RegisterRequest registerRequest);

	String rememberUserId(RememberUserIdRequest rememberUserIdRequest);

	String forgotPasswordBySecurityQuestion(
			ForgotPasswordBySecurityQuestionRequest forgotPasswordBySecurityQuestionRequest);

	String resetPasswordBySecurityQuestion(
			ResetPasswordBySecurityQuestionRequest resetPasswordBySecurityQuestionRequest);
}