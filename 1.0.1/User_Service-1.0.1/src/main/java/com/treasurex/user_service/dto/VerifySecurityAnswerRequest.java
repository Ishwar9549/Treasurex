package com.treasurex.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifySecurityAnswerRequest {

	@NotBlank(message = "Login ID (User ID / Email / Phone) cannot be blank")
	private String loginId;

	@NotBlank(message = "security Answer cannot be blank")
	private String securityAnswer;
}
//END