package com.example.demo.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.example.demo.entity.User;

@Component
public class UserMapper {

	private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

	public User dtoToEntity(UserDto dto) {
		logger.info("Mapping UserDto to User entity.");
		return User.builder().name(dto.getName()).email(dto.getEmail()).password(dto.getPassword())
				.build();
	}

	public UserDto entityToDto(User user) {
		logger.info("Mapping User entity to UserDto.");
		return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPassword());
	}
}
//100%