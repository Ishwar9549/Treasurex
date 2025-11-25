package com.treasurex.login_service.dao;

import java.util.Optional;

import com.treasurex.login_service.entity.User;

public interface UserDao {
	
	User save(User user);

	Optional<User> findByUserId(String userId);

	Optional<User> findByEmail(String email);

	Optional<User> findByPhoneNumber(String phoneNumber);

	boolean existsByUserId(String userId);
}
