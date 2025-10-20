package com.treasurex.login_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.treasurex.login_service.client")
public class LoginService {
	public static void main(String[] args) {
		SpringApplication.run(LoginService.class, args);
		System.err.println("Login Service AppApplication started.... testing");
	}
}