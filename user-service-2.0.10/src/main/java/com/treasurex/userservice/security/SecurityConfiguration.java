package com.treasurex.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Configures Spring Security for JWT-based authentication: - Stateless sessions
 * - Custom forbidden handling - Password encoding - Public endpoints (Swagger,
 * /public/**, /admin/**)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtAuthFilter jwtAuthFilter;

	/**
	 * Configures HTTP security: - Permits public endpoints (Swagger, /public/**,
	 * etc.) - Requires authentication for all other endpoints - Adds JWT filter
	 * before UsernamePasswordAuthenticationFilter - Stateless session management
	 *
	 * @param http HttpSecurity builder
	 * @return configured SecurityFilterChain
	 * @throws Exception on configuration errors
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// Custom entry point for unauthorized/forbidden requests
		AuthenticationEntryPoint forbiddenEntryPoint = (request, response, authException) -> {
			// Optional: log unauthorized access attempts
			// log.warn("Unauthorized access attempt to {}: {}", request.getRequestURI(),
			// authException.getMessage());
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Authentication Failed");
		};

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/public/**", "/admin/**", "/app-config/**", "/feature_flags/**",
								"/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/index.html")
						.permitAll().anyRequest().authenticated())
				.exceptionHandling(exception -> exception.authenticationEntryPoint(forbiddenEntryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Password encoder bean for hashing passwords (BCrypt with strength 5).
	 * Recommended: Strength 10+ in production for stronger security.
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(5);
	}

	/**
	 * AuthenticationManager bean required for login/authentication processing.
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
		return builder.getAuthenticationManager();
	}
}
//END