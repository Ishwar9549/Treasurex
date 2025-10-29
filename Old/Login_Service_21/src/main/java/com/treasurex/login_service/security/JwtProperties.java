package com.treasurex.login_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "treasurex.jwt")
public class JwtProperties {

	@NotBlank
	private String secretKey;

	@Positive
	private int registrationExpiryMinutes;

	@Positive
	private int accessExpiryMinutes;
}
//END