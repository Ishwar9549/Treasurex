package com.treasurex.feature_flag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the TreasureX Feature Flag Micro_service. Responsible for
 * managing feature toggles across other services.
 */
@SpringBootApplication
public class FeatureFlagApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeatureFlagApplication.class, args);
		System.err.println("TreasureX Feature Flag Service 1.0.2 Running Successfully...");
	}
}
//END
