package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for retrieving a forgotten username. Accepts either email or
 * phone number.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RememberUserNameRequest {

	@NotBlank(message = "Email / Phone cannot be blank")
	@Size(max = 100, message = "Login ID must not exceed 100 characters")
	@Pattern(regexp = "^(^[6-9]\\d{9}$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$)$", message = "Login ID must be a valid email or Indian phone number")
	private String loginId;

}
// END
