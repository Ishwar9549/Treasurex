package com.treasurex.userservice.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.treasurex.userservice.enums.JwtPurpose;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtAuthFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	/**
	 * Filter executed once per request to validate JWT tokens from Authorization
	 * header. If the token is valid, sets the Authentication object in
	 * SecurityContext.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		// Check if Authorization header is present and starts with "Bearer "
		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7); // Extract JWT without "Bearer " prefix

			if (jwtUtil.validateToken(token)) {

				// Extract subject (usually username/email) and purpose from token
				String subject = jwtUtil.extractSubject(token);
				JwtPurpose purpose = jwtUtil.extractPurpose(token);

				// Create authentication token with empty authorities
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject,
						purpose, // store purpose as credentials
						Collections.emptyList());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// Set the authentication in SecurityContext
				SecurityContextHolder.getContext().setAuthentication(authentication);

				// Highly recommended: log authentication success for monitoring
				log.debug("JWT validated for subject: {}, purpose: {}", subject, purpose);
			} else {
				// Optional: log invalid token attempts
				log.warn("Invalid JWT token received: {}", token);
			}
		} else {
			// Optional: log missing or malformed Authorization headers
			log.debug("No JWT token found in request header or header malformed");
		}

		// Continue filter chain
		filterChain.doFilter(request, response);
	}
}
//END