package com.treasurex.app_config.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing an application configuration stored in database. Supports
 * dynamic APP-level or microservice-level configurations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfig {

	private Long id;

	// Example: "MAX_LOGIN_ATTEMPTS"
	private String keyName;

	// Stored as String for flexibility
	private String value;

	// What this config does
	private String description;

	// "STRING", "BOOLEAN", "INTEGER" etc.
	private String type;
}
//END