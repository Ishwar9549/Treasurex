package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.UserDto;
import com.treasurex.login_service.dto.UserLoginRequest;
import com.treasurex.login_service.dto.UserRegisterRequest;
import com.treasurex.login_service.dto.VerifyUserRequest;

public interface UserService {
	
	String login(UserLoginRequest userLoginRequest);

	String register(UserRegisterRequest userRegisterRequest);

	String forgotPassword(String email);

	String resetPassword(String code, String email, String newPassword);

	String verifyUser(VerifyUserRequest verifyUserRequest);
	
	String changePassword(ChangePasswordRequest changePasswordRequest);
}