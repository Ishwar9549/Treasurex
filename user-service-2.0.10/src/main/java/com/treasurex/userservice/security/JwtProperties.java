package com.treasurex.userservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * JWT-related properties loaded from application configuration. Configurable
 * via application.yml / application.properties with prefix: treasurex.jwt
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "treasurex.jwt")
public class JwtProperties {

	/** Secret key used to sign JWTs. Must be a secure, random string. */
	@NotBlank
	private String secretKey;

	/**
	 * Expiry duration (in minutes) for registration OTPs / verification tokens.
	 * Used when generating OTP JWTs for email/phone verification.
	 */
	@Positive
	private int registrationExpiryMinutes;

	/**
	 * Expiry duration (in minutes) for access tokens. Controls how long JWT access
	 * tokens are valid for authenticated requests.
	 */
	@Positive
	private int accessExpiryMinutes;
}
//END