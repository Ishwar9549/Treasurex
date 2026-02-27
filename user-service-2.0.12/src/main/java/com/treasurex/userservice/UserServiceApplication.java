package com.treasurex.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.treasurex")
public class UserServiceApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(UserServiceApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
		System.err.println("Treasurex User Service 2.0.11 started successfully");
	}

	@PostConstruct
	public void started() {
		log.info("Treasurex User Service 2.0.11 started successfully");
	}
}
//END
