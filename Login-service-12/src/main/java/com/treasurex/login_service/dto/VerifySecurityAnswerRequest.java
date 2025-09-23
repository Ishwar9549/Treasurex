package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifySecurityAnswerRequest {
	
	@NotBlank(message = "user Id cannot be blank")
    private String userId;
	
	@NotBlank(message = "security Answer cannot be blank")
    private String securityAnswer;
}