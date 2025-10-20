package com.treasurex.feature_flag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for creating a new Feature Flag.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlagRequest {

	@NotBlank(message = "Feature flag name cannot be blank")
	private String name; // e.g., "OTP_RESEND_ENABLED"

	@NotNull(message = "Enabled status must be specified (true or false)")
	private Boolean enabled;

	@NotNull(message = "Description should not be null")
	private String description;
}
