package com.treasurex.app_config.service;

import java.util.List;
import com.treasurex.app_config.dto.AppConfigRequest;
import com.treasurex.app_config.entity.AppConfig;

public interface AppConfigService {

	String addOrUpdateConfig(AppConfigRequest request);

	AppConfig getConfigByKey(String key);

	List<AppConfig> getAllConfigs();

	boolean deleteConfig(String key);
}
