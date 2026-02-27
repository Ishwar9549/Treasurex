package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for resetting a user's MPIN. Used in secure recovery flows where
 * MPIN is updated after password verification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetMpinRequest {

	private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

	@NotBlank(message = "Password cannot be blank")
	@Pattern(regexp = PASSWORD_REGEX, message = "Password must be 8-20 characters and include uppercase, lowercase, digit, and special character")
	private String password;

	@NotBlank(message = "New MPIN cannot be blank")
	@Pattern(regexp = "^[0-9]{4}$", message = "MPIN must be exactly 4 digits")
	private String newMpin;

	@NotBlank(message = "Confirm MPIN cannot be blank")
	@Pattern(regexp = "^[0-9]{4}$", message = "MPIN must be exactly 4 digits")
	private String confirmMpin;
}
// END
