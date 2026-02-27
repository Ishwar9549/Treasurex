package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for changing user account password. Used in authenticated flows
 * and validated before processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

	private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

	@NotBlank(message = "Old password cannot be blank")
	@Size(max = 20, message = "Password must not exceed 20 characters")
	private String oldPassword;

	@NotBlank(message = "New password cannot be blank")
	@Pattern(regexp = PASSWORD_REGEX, message = "Password must be 8-20 characters and include uppercase, lowercase, digit, and special character")
	private String newPassword;

	@NotBlank(message = "Confirm password cannot be blank")
	@Pattern(regexp = PASSWORD_REGEX, message = "Password must be 8-20 characters and include uppercase, lowercase, digit, and special character")
	private String confirmPassword;
}
// END
