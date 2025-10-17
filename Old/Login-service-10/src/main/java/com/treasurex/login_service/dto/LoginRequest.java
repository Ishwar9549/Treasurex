package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

	@NotBlank(message = "userId cannot be blank")
	private String userId;

	@NotBlank(message = "Password cannot be blank")
	private String password;
}
