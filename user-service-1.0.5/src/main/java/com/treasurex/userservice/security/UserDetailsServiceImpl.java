package com.treasurex.userservice.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.treasurex.userservice.entity.User;
import com.treasurex.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of Spring Security's UserDetailsService to load user details
 * during authentication.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * Load a user by username (currently using email).
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

		// Build UserDetails with email, password, and fixed role "USER"
		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPassword()).roles("USER").build();
	}
}
//END