package com.treasurex.userservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Swagger / OpenAPI configuration for TreasureX User Service. Defines API
 * documentation metadata for Swagger UI.
 */
@Configuration
public class SwaggerConfig {

	private static final String API_TITLE = "TreasureX User Service API";
	private static final String API_VERSION = "2.0.13";
	private static final String API_DESCRIPTION = "APIs for user registration, login, OTP verification, password reset, and profile management";

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components())
				.info(new Info().title(API_TITLE).version(API_VERSION).description(API_DESCRIPTION)
						.contact(new Contact().name("TreasureX Support").email("support@treasurex.com")
								.url("https://www.treasurex.com"))
						.license(new License().name("TreasureX Internal License").url("https://www.treasurex.com")));
	}
}
// END
