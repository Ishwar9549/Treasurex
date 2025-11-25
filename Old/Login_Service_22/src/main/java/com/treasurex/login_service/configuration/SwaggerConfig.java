package com.treasurex.login_service.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components())
				.info(new Info().title("Treasurex Login Service API").version("1.0").description(
						"APIs for user registration, login, OTP verification, password reset, and profile management")
						.contact(new Contact().name("Treasurex Support").email("support@treasurex.com")
								.url("https://www.treasurex.com")));
	}
}
//END