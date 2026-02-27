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

	private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

	@NotBlank(message = "New password cannot be blank")
	@Pattern(regexp = PASSWORD_REGEX, message = "Password must be 8-20 characters and include uppercase, lowercase, digit, and special character")
	private String newPassword;

	@NotBlank(message = "Confirm password cannot be blank")
	@Pattern(regexp = PASSWORD_REGEX, message = "Password must be 8-20 characters and include uppercase, lowercase, digit, and special character")
	private String confirmPassword;
}
// END
