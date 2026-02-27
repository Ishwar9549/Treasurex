package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
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
	@Size(min = 6, message = "Username must be at least 6 characters long")
	private String userName;
}
//END