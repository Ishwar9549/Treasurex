package com.treasurex.login_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginService {
	public static void main(String[] args) {
		SpringApplication.run(LoginService.class, args);
		System.err.println("Login Service AppApplication started....");
	}
}