package com.treasurex.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for registering a user via email. Used in registration and
 * verification flows.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterEmailRequest {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;
}
// END
