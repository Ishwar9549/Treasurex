package com.treasurex.app_config.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfigRequest {
	
	@NotBlank(message = "App Config Key name cannot be blank")
	private String keyName;

	@NotBlank(message = "App Config Value cannot be blank")
	private String value;

	@NotBlank(message = "App Config description cannot be blank")
	private String description;
	
	@NotBlank(message = "App Config type cannot be blank")
	private String type;
}
