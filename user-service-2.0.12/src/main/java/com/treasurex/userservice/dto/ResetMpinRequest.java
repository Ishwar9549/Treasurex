package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for resetting a user's MPIN and password. Used in secure recovery
 * flows where both credentials are updated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetMpinRequest {

	@NotBlank(message = "Password cannot be blank")
	private String password;

	@NotBlank(message = "New MPIN cannot be blank")
	private String newMpin;

	@NotBlank(message = "Confirm MPIN cannot be blank")
	private String confirmMpin;
}
//END