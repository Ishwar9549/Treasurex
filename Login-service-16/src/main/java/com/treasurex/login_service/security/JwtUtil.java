package com.treasurex.login_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
@SuppressWarnings("deprecation")
public class JwtUtil {
	private final String SECRET = "this_is_a_very_secret_key_1234567890";
	private final long EXPIRATION_TIME = 86400000; // 1 day

	// ----------------------------------------------------------------------
	private final SecretKey key;
	private final int registrationExpiryMinutes;
	private final int accessExpiryMinutes;

	public JwtUtil(@Value("${app.jwt.secret}") String base64Secret,
			@Value("${app.jwt.registration.expiry-minutes}") int registrationExpiryMinutes,
			@Value("${app.jwt.access.expiry-minutes}") int accessExpiryMinutes) {
		this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(base64Secret));
		this.registrationExpiryMinutes = registrationExpiryMinutes;
		this.accessExpiryMinutes = accessExpiryMinutes;
	}

	public String generateToken(String subject, String purpose, int minutesValid) {
		long now = System.currentTimeMillis();
		Date exp = new Date(now + minutesValid * 60L * 1000L);

		return Jwts.builder().setSubject(subject).claim("purpose", purpose).setIssuedAt(new Date(now))
				.setExpiration(exp).signWith(key).compact();
	}

	public String generateRegistrationToken(String userId) {
		return generateToken(userId, "REGISTRATION", registrationExpiryMinutes);
	}

	public String generateAccessToken(String userId) {
		return generateToken(userId, "ACCESS", accessExpiryMinutes);
	}

	public String validateTokenAndGetSubject(String token, String expectedPurpose) {
		try {
			Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

			String purpose = claims.get("purpose", String.class);
			if (purpose == null || !purpose.equals(expectedPurpose)) {
				throw new JwtException("Invalid token purpose");
			}
			return claims.getSubject();
		} catch (ExpiredJwtException e) {
			throw new RuntimeException("Token expired", e);
		} catch (JwtException e) {
			throw new RuntimeException("Invalid token", e);
		}
	}

	public String validateTokenAndGetSubject(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

			return claims.getSubject();
		} catch (Exception e) {
			throw new RuntimeException("Invalid token", e);
		}
	}

	// ----------------------------------------------------------------------

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}

	public String generateToken(String email) {
		return Jwts.builder().setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractEmail(String token) {
		return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
