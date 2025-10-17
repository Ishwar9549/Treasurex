package com.treasurex.feature_flag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for fetching a feature flag by name.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlagByNameRequest {

	@NotBlank(message = "Feature flag name cannot be blank")
	private String name; // e.g., "LOGIN_ENABLED"
}
