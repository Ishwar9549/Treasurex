package com.treasurex.login_service.dto;

import org.springframework.stereotype.Component;

import com.treasurex.login_service.entity.AppConfig;

/**
 * Maps AppConfigRequest DTO to AppConfig Entity
 */
@Component
public class AppConfigMapper {

	public AppConfig toEntity(AppConfigRequest request) {
		return AppConfig.builder().keyName(request.getKeyName()).value(request.getValue())
				.description(request.getDescription()).type(request.getType()).build();
	}
}
//END