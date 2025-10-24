package com.treasurex.app_config.service;

import java.util.List;
import com.treasurex.app_config.dto.ApiResponse;
import com.treasurex.app_config.dto.AppConfigRequest;
import com.treasurex.app_config.entity.AppConfig;

/**
 * Service interface for managing AppConfig entities. Provides CRUD operations
 * with validation and business rules.
 */
public interface AppConfigService {

	/**
	 * Create a new AppConfig. Throws exception if keyName already exists.
	 */
	ApiResponse<Void> createAppConfig(AppConfigRequest request);

	/**
	 * Retrieve all AppConfigs.
	 */
	ApiResponse<List<AppConfig>> getAllAppConfig();

	/**
	 * Retrieve AppConfig by keyName. Throws exception if not found.
	 */
	ApiResponse<AppConfig> getAppConfigByName(String keyName);

	/**
	 * Update an existing AppConfig. Throws exception if keyName does not exist.
	 */
	ApiResponse<Void> updateAppConfig(AppConfigRequest request);

	/**
	 * Delete AppConfig by keyName. Throws exception if keyName does not exist.
	 */
	ApiResponse<Void> deleteAppConfig(String keyName);
}
//END