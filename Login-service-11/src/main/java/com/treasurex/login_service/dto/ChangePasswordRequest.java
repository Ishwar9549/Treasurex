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
public class ChangePasswordRequest {

	@NotBlank(message = "user Id cannot be blank")
	private String userId;

	@NotBlank(message = "new Password cannot be blank")
	private String newPassword;

	@NotBlank(message = "old Password cannot be blank")
	private String oldPassword;
}
