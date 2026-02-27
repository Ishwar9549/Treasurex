package com.treasurex.payment_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for Payment Gateway Spring Boot application.
 */
@Slf4j
@SpringBootApplication
public class PaymentGatewayApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PaymentGatewayApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentGatewayApplication.class, args);
	}

	@PostConstruct
	public void started() {
		log.info("Payment Gateway Service 1.0.6 started successfully");
	}
}
//END