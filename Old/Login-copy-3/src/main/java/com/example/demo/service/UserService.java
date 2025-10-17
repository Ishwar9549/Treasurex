package com.example.demo.service;

import com.example.demo.dto.UserDto;

public interface UserService {

    UserDto login(String email, String password);

    UserDto register(UserDto userDto);

    String forgotPassword(String email);

	String resetPassword(String code, String email, String newPassword);
}
//100%