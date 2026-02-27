package com.treasurex.app_config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for TreasureX App Config Service.
 */
@SpringBootApplication
@MapperScan("com.treasurex.app_config.mapper") // Scans MyBatis mapper interfaces in the specified package
@Slf4j // Lombok annotation for SLF4J logger
//Swagger/OpenAPI metadata for API documentation
@OpenAPIDefinition(info = @Info(title = "TreasureX App Config Service", version = "1.0.5", description = "APIs for TreasureX App Configuration Service - Manage dynamic application configurations"))
public class AppConfigApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(AppConfigApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(AppConfigApplication.class, args);
	}

	@PostConstruct
	public void started() {
		log.info("TreasureX App Config Service 1.0.5 Running Successfully...");
	}
}
//END