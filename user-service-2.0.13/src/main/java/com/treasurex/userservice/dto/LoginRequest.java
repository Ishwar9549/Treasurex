package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user authentication using email or phone number.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

	@NotBlank(message = "loginId cannot be blank")
	@Size(max = 100, message = "Login ID must not exceed 100 characters")
	private String loginId;

	@NotBlank(message = "Password cannot be blank")
	@Size(max = 20, message = "Password must not exceed 20 characters")
	private String password;
}
// END
