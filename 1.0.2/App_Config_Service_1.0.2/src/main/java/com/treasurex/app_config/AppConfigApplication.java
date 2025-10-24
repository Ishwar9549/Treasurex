package com.treasurex.app_config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppConfigApplication.class, args);
		System.err.println("TreasureX App Config Service 1.0.2 Running Successfully...");
	}
}
//END