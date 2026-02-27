package com.treasurex.userservice.security;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.treasurex.userservice.enums.JwtPurpose;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for generating, validating, and extracting data from JWTs.
 * Supports multiple purposes (registration, password reset, MPIN, access, etc.)
 */
@Slf4j
@Component
public class JwtUtil {

	private final SecretKey key;
	private final int accessExpiryMinutes;
	private final int registrationExpiryMinutes;

	public JwtUtil(JwtProperties props) {
		// Decode secret key from Base64 and create HMAC-SHA key
		this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(props.getSecretKey()));
		this.accessExpiryMinutes = props.getAccessExpiryMinutes();
		this.registrationExpiryMinutes = props.getRegistrationExpiryMinutes();
	}

	/* ================= TOKEN GENERATION ================= */

	/**
	 * Generates JWT token with subject, purpose, and optional extra claims. Expiry
	 * depends on the purpose (ACCESS_PROFILE uses access token expiry, others use
	 * registration expiry).
	 *
	 * @param subject     Subject of the token (usually username or email)
	 * @param purpose     Enum representing the token purpose
	 * @param extraClaims Optional custom claims to include in the JWT
	 * @return Signed JWT string
	 */
	public String generateToken(String subject, JwtPurpose purpose, Map<String, Object> extraClaims) {
		long now = System.currentTimeMillis();

		// Determine token expiry based on purpose
		int expiryMinutes = (purpose == JwtPurpose.ACCESS_PROFILE) ? accessExpiryMinutes : registrationExpiryMinutes;
		Date expiry = new Date(now + expiryMinutes * 60L * 1000L);

		JwtBuilder builder = Jwts.builder().subject(subject).claim("purpose", purpose.name()).issuedAt(new Date(now))
				.expiration(expiry);

		// Add extra claims safely
		if (extraClaims != null) {
			extraClaims.forEach(builder::claim);
		}

		String token = builder.signWith(key).compact();

		// Optional: log token generation for debug (do not log token in production!)
		log.debug("Generated JWT for subject: {}, purpose: {}", subject, purpose);

		return token;
	}

	/* ================= VALIDATION ================= */

	/**
	 * Validates a JWT token signature and structure.
	 *
	 * @param token JWT string
	 * @return true if token is valid, false otherwise
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			// Highly recommended: log invalid token attempts for security monitoring
			log.warn("Invalid JWT: {}", e.getMessage());
			return false;
		}
	}

	/* ================= EXTRACTION ================= */

	/**
	 * Extracts all claims from the JWT.
	 *
	 * @param token JWT string
	 * @return Claims object containing all payload data
	 */
	public Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	/**
	 * Extracts the subject (username/email) from JWT.
	 */
	public String extractSubject(String token) {
		return extractAllClaims(token).getSubject();
	}

	/**
	 * Extracts the token purpose (JwtPurpose enum) from JWT.
	 */
	public JwtPurpose extractPurpose(String token) {
		String purpose = extractAllClaims(token).get("purpose", String.class);
		return JwtPurpose.valueOf(purpose);
	}

	/**
	 * Extracts a boolean claim from JWT. Returns false if claim is missing or null.
	 *
	 * @param token JWT string
	 * @param claim Claim key
	 * @return Boolean value of the claim
	 */
	public boolean extractBooleanClaim(String token, String claim) {
		Boolean value = extractAllClaims(token).get(claim, Boolean.class);
		return value != null && value;
	}
}
//END