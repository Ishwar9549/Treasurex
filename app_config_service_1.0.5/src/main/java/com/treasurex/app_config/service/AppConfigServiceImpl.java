package com.treasurex.app_config.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.treasurex.app_config.dto.ApiResponse;
import com.treasurex.app_config.dto.AppConfigConverter;
import com.treasurex.app_config.dto.AppConfigRequest;
import com.treasurex.app_config.exception.ResourceConflictException;
import com.treasurex.app_config.exception.ResourceNotFoundException;
import com.treasurex.app_config.mapper.AppConfigMapper;
import com.treasurex.app_config.model.AppConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for AppConfig operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppConfigServiceImpl implements AppConfigService {

	private final AppConfigMapper appConfigMapper;
	private final AppConfigConverter converter;

	/**
	 * Create a new AppConfig entry.
	 */
	@Override
	public ApiResponse<Void> createAppConfig(AppConfigRequest request) {

		log.debug("Creating AppConfig with keyName={}", request.getKeyName());

		if (appConfigMapper.findByKeyName(request.getKeyName()) != null) {
			log.warn("AppConfig already exists with keyName={}", request.getKeyName());
			throw new ResourceConflictException("AppConfig with keyName '" + request.getKeyName() + "' already exists");
		}

		AppConfig entity = converter.toEntity(request);
		appConfigMapper.insert(entity);

		log.info("AppConfig created successfully with keyName={}", request.getKeyName());
		return ApiResponse.created(null, "AppConfig created successfully");
	}

	/**
	 * Fetch all AppConfig entries.
	 */
	@Override
	public ApiResponse<List<AppConfig>> getAllAppConfig() {

		log.debug("Fetching all AppConfig records");
		return ApiResponse.success(appConfigMapper.findAll(), "All AppConfigs retrieved successfully");
	}

	/**
	 * Fetch AppConfig by key name.
	 */
	@Override
	public ApiResponse<AppConfig> getAppConfigByName(String keyName) {

		log.debug("Fetching AppConfig with keyName={}", keyName);

		AppConfig config = appConfigMapper.findByKeyName(keyName);
		if (config == null) {
			log.warn("AppConfig not found with keyName={}", keyName);
			throw new ResourceNotFoundException("AppConfig with keyName '" + keyName + "' not found");
		}

		return ApiResponse.success(config, "AppConfig retrieved successfully");
	}

	/**
	 * Update an existing AppConfig.
	 */
	@Override
	public ApiResponse<Void> updateAppConfig(AppConfigRequest request) {

		log.debug("Updating AppConfig with keyName={}", request.getKeyName());

		AppConfig existing = appConfigMapper.findByKeyName(request.getKeyName());
		if (existing == null) {
			log.warn("Cannot update. AppConfig not found with keyName={}", request.getKeyName());
			throw new ResourceNotFoundException("AppConfig with keyName '" + request.getKeyName() + "' not found");
		}

		AppConfig updated = converter.toEntity(request);
		updated.setId(existing.getId());

		appConfigMapper.update(updated);

		log.info("AppConfig updated successfully with keyName={}", request.getKeyName());
		return ApiResponse.success(null, "AppConfig updated successfully");
	}

	/**
	 * Delete AppConfig by key name.
	 */
	@Override
	public ApiResponse<Void> deleteAppConfig(String keyName) {

		log.debug("Deleting AppConfig with keyName={}", keyName);

		AppConfig existing = appConfigMapper.findByKeyName(keyName);
		if (existing == null) {
			log.warn("Cannot delete. AppConfig not found with keyName={}", keyName);
			throw new ResourceNotFoundException("AppConfig with keyName '" + keyName + "' not found");
		}

		appConfigMapper.deleteById(existing.getId());

		log.info("AppConfig deleted successfully with keyName={}", keyName);
		return ApiResponse.success(null, "AppConfig deleted successfully");
	}
}
//END