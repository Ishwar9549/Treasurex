package com.treasurex.datafetch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for the TreasureX Commodities Data Fetch service.
 */
@SpringBootApplication

@Slf4j // Enables SLF4J logging via Lombok

// Swagger / OpenAPI metadata for API documentation
@OpenAPIDefinition(info = @Info(title = "TreasureX Commodities Data Fetch Service", version = "1.0.4", description = "APIs for fetching commodities data"))
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void started() {
		log.info("TreasureX Commodities service started successfully..1.0.5");
	}
}
//END