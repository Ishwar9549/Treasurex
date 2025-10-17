package com.treasurex.login_service.dto;

import org.springframework.stereotype.Component;

import com.treasurex.login_service.entity.User;

@Component
public class UserMapper {

	public UserDto entityToDto(User user) {
		if (user == null) {
			return null;
		}
		return UserDto.builder().name(user.getFirstName()).email(user.getEmail()).id(user.getId())
				.password(user.getPassword()).build();
	}

	public User dtoToEntity(RegisterRequest request) {
		if (request == null) {
			return null;
		}
		return User.builder().userId(request.getUserId()).firstName(request.getFirstName())
				.middleName(request.getMiddleName()).lastName(request.getLastName()).email(request.getEmail())
				.password(request.getPassword()).phoneNumber(request.getPhoneNumber())
				.alternativePhoneNumber(request.getAlternativePhoneNumber())
				.alternativeEmail(request.getAlternativeEmail()).dob(request.getDob()).address(request.getAddress())
				.idType(request.getIdType()).idNumber(request.getIdNumber()).build();
	}
}