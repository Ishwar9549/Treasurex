package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.UserDto;
import com.treasurex.login_service.dto.UserLoginRequest;
import com.treasurex.login_service.dto.UserRegisterRequest;

public interface UserService {
	String login(UserLoginRequest userLoginRequest);

	UserDto register(UserRegisterRequest userRegisterRequest);

	String forgotPassword(String email);

	String resetPassword(String code, String email, String newPassword);
}