package com.treasurex.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Find user by phone number
	 */
	Optional<User> findByPhoneNumber(String phoneNumber);

	/**
	 * Find user by email
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Find user by user name
	 */
	Optional<User> findByUserName(String userName);

	/**
	 * Check if a user with given username exists
	 */
	boolean existsByUserName(String userName);

}
//END 
