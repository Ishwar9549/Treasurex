package com.treasurex.userservice.security;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	private final SecretKey key;
	private final int accessExpiryMinutes;
	private final int registrationExpiryMinutes;

	public JwtUtil(JwtProperties props) {
		this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(props.getSecretKey()));
		this.accessExpiryMinutes = props.getAccessExpiryMinutes();
		this.registrationExpiryMinutes = props.getRegistrationExpiryMinutes();
	}

	/* ================= TOKEN GENERATION ================= */

	public String generateToken(String subject, JwtPurpose purpose, Map<String, Object> extraClaims) {
		long now = System.currentTimeMillis();

		int expiryMinutes = (purpose == JwtPurpose.ACCESS_PROFILE) ? accessExpiryMinutes : registrationExpiryMinutes;

		Date expiry = new Date(now + expiryMinutes * 60L * 1000L);

		JwtBuilder builder = Jwts.builder().subject(subject).claim("purpose", purpose.name()).issuedAt(new Date(now))
				.expiration(expiry);

		// ✅ SAFE way to add custom claims
		if (extraClaims != null) {
			extraClaims.forEach(builder::claim);
		}

		return builder.signWith(key).compact();
	}

	/* ================= VALIDATION ================= */

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("Invalid JWT: {}", e.getMessage());
			return false;
		}
	}

	/* ================= EXTRACTION ================= */

	public Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	public String extractSubject(String token) {
		return extractAllClaims(token).getSubject();
	}

	public JwtPurpose extractPurpose(String token) {
		String purpose = extractAllClaims(token).get("purpose", String.class);
		return JwtPurpose.valueOf(purpose);
	}

	public boolean extractBooleanClaim(String token, String claim) {
		Boolean value = extractAllClaims(token).get(claim, Boolean.class);
		return value != null && value;
	}
}
