package com.example.Login_Page.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Login_Page.Entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
