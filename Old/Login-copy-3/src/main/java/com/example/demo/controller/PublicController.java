package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.UserDto;
import com.example.demo.security.JwtHelperUtility;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

	private static final Logger logger = LoggerFactory.getLogger(PublicController.class);

	private final UserService userService;

	private final AuthenticationManager authenticationManager;
	private final JwtHelperUtility jwtHelper;
	private final UserDetailsService userDetailsService;

	@GetMapping("/test")
	public String publicFunctionOne() {
		logger.info("Public endpoint accessed.");
		return "test one Public endpoint accessed.";
	}

	@PostMapping("/register")
	public ResponseEntity<UserDto> userRegister(@RequestBody UserDto userDto) {
		logger.info("Public register endpoint called.");
		UserDto registeredUser = userService.register(userDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
	}

	@PostMapping("/login1")
	public ResponseEntity<UserDto> userLogin(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		logger.info("Public login endpoint called for email: {}", email);
		return ResponseEntity.ok(userService.login(email, password));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
		logger.info("Forgot password endpoint called for email: {}", email);
		String resetCode = userService.forgotPassword(email);
		String str = resetCode + " copy this code and reset with new password";
		return ResponseEntity.ok(str); // In future, send via email
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword, @RequestParam("code") String code) {
		logger.info("Reset password endpoint called for email: {}", email);
		userService.resetPassword(code, email, newPassword);
		return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
	}

	@PostMapping("/forgot-password-new")
	public ResponseEntity<String> forgotPasswordNew(@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword) {
		logger.info("New Forgot password endpoint called for email: {}", email);
		String resetCode = userService.forgotPassword(email);
		String url = "click this url and reset the password " + "http://localhost:9090/public/reset-password?email="
				+ email + "&newPassword=" + newPassword + "&code=" + resetCode;
		return ResponseEntity.ok(url); // In future, send via email
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> getToken(@RequestBody AuthRequest req) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
		UserDetails ud = userDetailsService.loadUserByUsername(req.getEmail());
		String token = jwtHelper.generateToken(ud);
		return ResponseEntity.ok(new AuthResponse(token));
	}
}
//100%