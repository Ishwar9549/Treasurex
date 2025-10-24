package com.treasurex.login_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.login_service.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserId(String userId);

	Optional<User> findByEmail(String email);

	Optional<User> findByPhoneNumber(String phoneNumber);

	boolean existsByUserId(String userId);
}
//END