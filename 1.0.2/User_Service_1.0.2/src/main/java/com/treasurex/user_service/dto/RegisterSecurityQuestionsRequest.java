package com.treasurex.user_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterSecurityQuestionsRequest {

	@NotBlank(message = "Login ID (User ID / Email / Phone) cannot be blank")
	private String loginId;

	@NotEmpty(message = "Security questions list cannot be empty")
	@Valid
	private List<SecurityQuestionDTO> securityQuestions;

	@Data
	public static class SecurityQuestionDTO {
		@NotBlank(message = "Question cannot be blank")
		private String question;

		@NotBlank(message = "Answer cannot be blank")
		private String answer;
	}
}
//END