package com.treasurex.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfig {

	private Long id;
	
	private String keyName; // Example: "MAX_LOGIN_ATTEMPTS"

	private String value; // Stored as String for flexibility

	private String description; // What this config does

	private String type; // "STRING", "BOOLEAN", "INTEGER" etc.
}
//END