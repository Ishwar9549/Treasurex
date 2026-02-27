package com.treasurex.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for initiating email-based recovery flows such as password or
 * MPIN reset.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;

	@NotBlank(message = "Purpose cannot be blank")
	@Pattern(regexp = "^(FORGOT_PASSWORD|FORGOT_MPIN)$", message = "Purpose must be FORGOT_PASSWORD or FORGOT_MPIN")
	private String purpose;
}
// END
