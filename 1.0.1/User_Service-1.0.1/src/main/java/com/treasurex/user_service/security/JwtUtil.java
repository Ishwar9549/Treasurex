package com.treasurex.user_service.security;

import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final SecretKey key;
	private final int registrationExpiryMinutes;
	private final int accessExpiryMinutes;

	public JwtUtil(JwtProperties jwtProperties) {
		this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecretKey()));
		this.registrationExpiryMinutes = jwtProperties.getRegistrationExpiryMinutes();
		this.accessExpiryMinutes = jwtProperties.getAccessExpiryMinutes();
	}

	private String generateToken(String subject, String purpose, int minutesValid) {
		long now = System.currentTimeMillis();
		Date exp = new Date(now + minutesValid * 60L * 1000L);

		return Jwts.builder().subject(subject).claim("purpose", purpose).issuedAt(new Date(now)).expiration(exp)
				.signWith(key).compact();
	}

	public String generateRegistrationToken(String userId) {
		return generateToken(userId, "REGISTRATION", registrationExpiryMinutes);
	}

	public String generateAccessToken(String userId) {
		return generateToken(userId, "ACCESS", accessExpiryMinutes);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String extractEmail(String token) {
		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return claims.getSubject();
	}

	public String validateTokenAndGetSubject(String token, String expectedPurpose) {
		try {
			Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

			String purpose = claims.get("purpose", String.class);
			if (purpose == null || !purpose.equals(expectedPurpose)) {
				throw new JwtException("Invalid token purpose");
			}

			return claims.getSubject();
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			throw new RuntimeException("Token expired", e);
		} catch (JwtException e) {
			throw new RuntimeException("Invalid token", e);
		}
	}
}
//END
