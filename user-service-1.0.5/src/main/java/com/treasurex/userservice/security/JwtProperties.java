package com.treasurex.userservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * JWT-related properties loaded from application configuration. Prefix:
 * treasurex.jwt
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "treasurex.jwt")
public class JwtProperties {

	/** Secret key used to sign JWTs */
	@NotBlank
	private String secretKey;

	/** Expiry duration (in minutes) for registration OTPs / verification tokens */
	@Positive
	private int registrationExpiryMinutes;

	/** Expiry duration (in minutes) for access tokens */
	@Positive
	private int accessExpiryMinutes;

}
//END 