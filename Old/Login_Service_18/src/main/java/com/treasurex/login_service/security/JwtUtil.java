package com.treasurex.login_service.security;

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
	private final int tokenExpiryMinutes;

	public JwtUtil(JwtProperties jwtProperties) {
		this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecretKey()));
		// use accessExpiryMinutes as general expiry
		this.tokenExpiryMinutes = jwtProperties.getAccessExpiryMinutes();
	}

	/** Generate one general purpose token for userId or email */
	public String generateToken(String subject) {
		long now = System.currentTimeMillis();
		Date exp = new Date(now + tokenExpiryMinutes * 60L * 1000L);

		return Jwts.builder().subject(subject).issuedAt(new Date(now)).expiration(exp).signWith(key).compact();
	}

	/** Validate JWT signature and expiry */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	/** Extract subject (userId/email) from token */
	public String extractSubject(String token) {
		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return claims.getSubject();
	}
}
