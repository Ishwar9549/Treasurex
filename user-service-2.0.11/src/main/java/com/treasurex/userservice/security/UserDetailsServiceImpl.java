package com.treasurex.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.treasurex.userservice.mapper.UserMapper;
import com.treasurex.userservice.model.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userMapper.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + username);
		} else {
			return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
					.password(user.getPassword()).roles("USER").build();
		}
	}
}
//END