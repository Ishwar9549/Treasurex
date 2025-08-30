package com.example.Login_Page.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.DTO.LoginRequest;
import com.example.DTO.PasswordResetRequest;
import com.example.DTO.RegisterRequest;
import com.example.Login_Page.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(token); // return token only on login
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request.getName(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }
    


    @PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
    	System.err.println("Forgot password endpoint called for email: "+ email);
		String resetCode = authService.forgotPassword(email);
		String str = resetCode + " copy this code and reset with new password";
		return ResponseEntity.ok(str); // In future, send via email
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword, @RequestParam("code") String code) {
		System.err.println("Reset password endpoint called for email:"+ email);
		authService.resetPassword(code, email, newPassword);
		return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
	}
	
	@PostMapping("/forgot-password-new")
	public ResponseEntity<String> forgotPasswordNew(@RequestParam("email") String email,@RequestParam("newPassword") String newPassword) {
		System.err.println("New Forgot password endpoint called for email: " +email);
		String resetCode = authService.forgotPassword(email);
		String url = "click this url and reset the password "+"http://localhost:9090/auth/reset-password?email="+email+"&newPassword="+newPassword+"&code="+resetCode;
		return ResponseEntity.ok(url); // In future, send via email
	}
}
