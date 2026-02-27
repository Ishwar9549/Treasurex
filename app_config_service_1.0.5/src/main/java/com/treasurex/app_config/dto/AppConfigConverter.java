package com.treasurex.app_config.dto;

import org.springframework.stereotype.Component;

import com.treasurex.app_config.model.AppConfig;

/**
 * Maps AppConfigRequest DTO to AppConfig Entity
 */
@Component
public class AppConfigConverter {

	public AppConfig toEntity(AppConfigRequest request) {
		return AppConfig.builder().keyName(request.getKeyName()).value(request.getValue())
				.description(request.getDescription()).type(request.getType()).build();
	}
}
//END