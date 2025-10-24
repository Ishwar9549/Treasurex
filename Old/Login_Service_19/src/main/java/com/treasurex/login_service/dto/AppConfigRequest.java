package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request object for creating/updating AppConfig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfigRequest {

	@NotBlank(message = "App Config keyName cannot be blank")
	private String keyName;

	@NotBlank(message = "App Config value cannot be blank")
	private String value;

	@NotBlank(message = "App Config description cannot be blank")
	private String description;

	@NotBlank(message = "App Config type cannot be blank")
	private String type;
}
//END