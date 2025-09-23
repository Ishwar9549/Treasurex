package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordBySecurityQuestionRequest {
	
	@NotBlank(message = "user Id cannot be blank")
    private String userId;
	
	@NotBlank(message = "new Password cannot be blank")
    private String newPassword;
	
	@NotBlank(message = "security Answer cannot be blank")
    private String securityAnswer;
}
