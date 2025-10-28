package com.treasurex.payment_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for TreasureX Payment Gateway Micro_service.
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.err.println("TreasureX Payment Gateway Service 1.0.2 Running Successfully...");
	}
}
