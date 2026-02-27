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

	private static final String APP_NAME = "TreasureX User Service";
	private static final String APP_VERSION = "2.0.13";

	/**
	 * Configures application when deployed to external Tomcat server.
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		log.info("{} v{} is initializing on external Tomcat", APP_NAME, APP_VERSION);
		return builder.sources(UserServiceApplication.class);
	}

	/**
	 * Main entry point for the TreasureX User Service application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
		System.err.println("Application started successfully.");
	}

	/**
	 * Executes after bean initialization to confirm successful startup.
	 */
	@PostConstruct
	public void started() {
		log.info("{} v{} started successfully", APP_NAME, APP_VERSION);
	}
}
// END
