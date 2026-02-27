package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for setting or resetting user MPIN. Used in secure, authenticated
 * or recovery flows.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MpinRequest {

	@NotBlank(message = "New MPIN cannot be blank")
	@Pattern(regexp = "^[0-9]{4}$", message = "MPIN must be exactly 4 digits")
	private String newMpin;

	@NotBlank(message = "Confirm MPIN cannot be blank")
	@Pattern(regexp = "^[0-9]{4}$", message = "MPIN must be exactly 4 digits")
	private String confirmMpin;
}
// END
