package com.treasurex.feature_flag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for the TreasureX Feature Flag Microservice. Responsible for
 * managing feature toggles across services.
 */
@SpringBootApplication
@Slf4j

// Swagger / OpenAPI configuration for API documentation
@OpenAPIDefinition(info = @Info(title = "TreasureX Feature Flag Service", version = "1.0.5", description = "APIs for managing feature flags across TreasureX services"))
public class FeatureFlagApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(FeatureFlagApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(FeatureFlagApplication.class, args);
	}

	@PostConstruct
	public void started() {
		log.info("TreasureX Feature Flag Service 1.0.5 Running Successfully...");
	}
}
//END
