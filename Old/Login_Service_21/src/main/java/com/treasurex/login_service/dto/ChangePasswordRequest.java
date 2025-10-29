package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

	// @NotBlank(message = "Login ID (User ID / Email / Phone) cannot be blank")
	// private String loginId;

	@NotBlank(message = "old Password cannot be blank")
	private String oldPassword;

	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character")
	@NotBlank(message = "new Password cannot be blank")
	private String newPassword;

	@NotBlank(message = "Confirm Password cannot be blank")
	private String confirmPassword;
}
//END