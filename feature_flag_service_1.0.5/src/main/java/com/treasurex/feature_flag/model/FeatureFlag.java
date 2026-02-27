package com.treasurex.feature_flag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model representing a Feature Flag in the database.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlag {

	private Long id;

	private String name; // e.g., "OTP_RESEND_ENABLED"

	private Boolean enabled; // true = ON, false = OFF

	private String description; // Optional description
}
//END