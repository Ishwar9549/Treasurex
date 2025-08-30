package com.example.Login_Page.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Login_Page.Entity.User;
import com.example.Login_Page.Repository.UserRepository;
//import com.example.Login_Page.entity.User;
import com.example.Login_Page.security.JwtUtil;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    private String code;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email, String password) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        return jwtUtil.generateToken(user.getEmail());
    }

    public String register(String name, String email, String password) {
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create and save user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        // Return JWT token after registration
        return jwtUtil.generateToken(user.getEmail());
    }
    
	public String forgotPassword(String email) {
		System.err.println("Forgot password service method executed for email: "+ email);

		userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));

		String resetCode = generateRandomCode(4); // Flexible length
		System.err.println("Generated password reset code for {"+ email + resetCode);

		// TODO: Send code via email in future
		return resetCode;
	}
	
	public void resetPassword(String code, String email, String newPassword) {
		System.err.println("Reset password service method executed for email:"+email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));

		if (this.code.equals(code)) 
		{
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			this.code = "Fail";
			System.err.println("Password updated successfully for email:"+ email);			
		}
		else
		{
			throw new RuntimeException("Code is not correct...");
		}
	}

	private String generateRandomCode(int length) {
		StringBuilder code = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			code.append(random.nextInt(10)); // digits 0–9
		}
		this.code = code.toString();
		return code.toString();
	}
}
