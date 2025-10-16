package com.treasurex.login_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.login_service.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserId(String userId);

	Optional<User> findByEmail(String email);

	Optional<User> findByPhoneNumber(String phoneNumber);
	
	boolean existsByUserId(String userId);
}