package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

	@NotBlank(message = "newPassword cannot be blank")
	private String newPassword;

	@NotBlank(message = "Email cannot be blank")
	private String userId;

	@NotBlank(message = "code cannot be blank")
	private String otp;
}