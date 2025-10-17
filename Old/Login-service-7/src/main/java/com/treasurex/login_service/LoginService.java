package com.treasurex.login_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class LoginService{

	public static void main(String[] args) {
		SpringApplication.run(LoginService.class, args);
		log.info("Login Service AppApplication started....");
		System.err.println("Login Service AppApplication started....");
	}
}   