package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

//Create JWTHelper  class This class contains method related to perform operations with JWT token like generateToken, validateToken etc.
@Component
public class JwtHelperUtility {

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // seconds
	private static final String SECRET = "this_is_a_very_secret_key_1234567890_this_is_a_very_secret_key_1234567890";

	private Key key() {
		return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
	}

	public String getUsernameFromToken(String token) {
		return getAllClaims(token).getSubject();
	}

	public Date getExpirationDateFromToken(String token) {
		return getAllClaims(token).getExpiration();
	}

	private Claims getAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
	}

	private boolean isTokenExpired(String token) {
		return getExpirationDateFromToken(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + JWT_TOKEN_VALIDITY * 1000);
		return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(now).setExpiration(exp)
				.signWith(key(), SignatureAlgorithm.HS512).compact();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		String username = getUsernameFromToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}
}
