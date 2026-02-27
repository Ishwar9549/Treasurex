package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for setting or resetting a user password. Used in secure and
 * recovery flows.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRequest {

	@NotBlank(message = "New password cannot be blank")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character")
	private String newPassword;

	@NotBlank(message = "Confirm password cannot be blank")
	private String confirmPassword;
}
//END