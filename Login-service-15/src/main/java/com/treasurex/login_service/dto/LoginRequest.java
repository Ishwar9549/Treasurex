package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

	@NotBlank(message = "Login ID (User ID / Email / Phone) cannot be blank")
	private String loginId;

	@NotBlank(message = "Password cannot be blank")
	private String password;
}