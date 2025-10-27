package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating or updating a Feature Flag. Includes input validations to
 * ensure clean and consistent data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlagRequest {

	@NotBlank(message = "Feature name is required")
	@Size(max = 100, message = "Feature name must be at most 100 characters")
	private String name; // Unique feature name, e.g., "OTP_RESEND_ENABLED"

	@NotNull(message = "Enabled status must be specified")
	private Boolean enabled; // true = ON, false = OFF

	@Size(max = 255, message = "Description cannot exceed 255 characters")
	private String description; // Optional short explanation

	// @NotBlank(message = "Environment is required")
	@Pattern(regexp = "DEV|STAGING|PROD", message = "Environment must be one of: DEV, STAGING, PROD")
	private String environment; // Where this flag applies

	private String targetRoles; // JSON array of allowed roles (optional)
	private String targetUsers; // JSON array of allowed users (optional)

	private Boolean archived; // Default: false
}
//END