package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserIdCheckRequest {

	@NotBlank(message = "userId cannot be blank")
	@Size(min = 6, message = "userId must be above 6 Char  ")
	private String userId;
}
