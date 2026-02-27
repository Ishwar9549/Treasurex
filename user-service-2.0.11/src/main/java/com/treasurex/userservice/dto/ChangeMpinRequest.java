package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for changing user MPIN. Used in authenticated flows and validated
 * before processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeMpinRequest {

	@NotBlank(message = "Old MPIN cannot be blank")
	private String oldMpin;

	@NotBlank(message = "New MPIN cannot be blank")
	private String newMpin;

	@NotBlank(message = "Confirm MPIN cannot be blank")
	private String confirmMpin;
}
//END