package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for checking if a username is available.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNameCheckRequest {

	@NotBlank(message = "Username cannot be blank")
	@Size(min = 6, max = 30, message = "Username must be between 6 and 30 characters")
	@Pattern(regexp = "^[A-Za-z0-9._]+$", message = "Username can contain only letters, numbers, dot and underscore")
	private String userName;
}
// END
