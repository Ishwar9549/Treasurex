package com.treasurex.feature_flag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for toggling an existing feature flag.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleFeatureFlagRequest {

	@NotBlank(message = "Feature flag name cannot be blank")
	private String name;
}
