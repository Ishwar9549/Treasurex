package com.treasurex.app_config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.err.println("TreasureX App Config Service 1.0.1 Running Successfully...");
	}
}
//END