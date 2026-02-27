package com.treasurex.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class UserServiceApplication {
	
	public static void main(String[] args) {
		
		SpringApplication.run(UserServiceApplication.class, args);
		
		System.err.println("Treasurex User Service 1.0.5 copy Started....");
		
		log.info("Treasurex User Service started...");
		
	}
}
//END