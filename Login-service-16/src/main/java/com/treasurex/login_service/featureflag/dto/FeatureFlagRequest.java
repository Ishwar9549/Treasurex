package com.treasurex.login_service.featureflag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlagRequest {

	@NotBlank(message = "Feature flag name cannot be blank")
	@Size(max = 100, message = "Feature flag name must be less than 100 characters")
	private String name; // e.g., "OTP_RESEND_ENABLED"

	@NotNull(message = "Enabled status must be specified (true or false)")
	private Boolean enabled; // true = ON, false = OFF

	@NotNull(message = "Description should not null")
	@Size(max = 255, message = "Description must be less than 255 characters")
	private String description;
}
