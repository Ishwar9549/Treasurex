package com.treasurex.login_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.treasurex.login_service.dao.AppConfigDao;
import com.treasurex.login_service.dto.ApiResponse;
import com.treasurex.login_service.dto.AppConfigMapper;
import com.treasurex.login_service.dto.AppConfigRequest;
import com.treasurex.login_service.entity.AppConfig;
import com.treasurex.login_service.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of AppConfigService. Handles business logic for AppConfig with
 * proper validations.
 */
@Service
@RequiredArgsConstructor
public class AppConfigServiceImpl implements AppConfigService {

	private final AppConfigDao appConfigDao;
	private final AppConfigMapper appConfigMapper;

	@Override
	public ApiResponse<Void> createAppConfig(AppConfigRequest request) {
		// Check uniqueness
		if (appConfigDao.findByKeyName(request.getKeyName()).isPresent()) {
			throw new ResourceNotFoundException("AppConfig with keyName '" + request.getKeyName() + "' already exists");
		}

		AppConfig entity = appConfigMapper.toEntity(request);
		appConfigDao.save(entity);
		return ApiResponse.created(null, "AppConfig created successfully");
	}

	@Override
	public ApiResponse<List<AppConfig>> getAllAppConfig() {
		List<AppConfig> configs = appConfigDao.findAll();
		return ApiResponse.success(configs, "All AppConfigs retrieved successfully");
	}

	@Override
	public ApiResponse<AppConfig> getAppConfigByName(String keyName) {
		AppConfig config = appConfigDao.findByKeyName(keyName)
				.orElseThrow(() -> new ResourceNotFoundException("AppConfig with keyName '" + keyName + "' not found"));
		return ApiResponse.success(config, "AppConfig retrieved successfully");
	}

	@Override
	public ApiResponse<Void> updateAppConfig(AppConfigRequest request) {
		AppConfig existing = appConfigDao.findByKeyName(request.getKeyName()).orElseThrow(
				() -> new ResourceNotFoundException("AppConfig with keyName '" + request.getKeyName() + "' not found"));

		AppConfig updated = appConfigMapper.toEntity(request);
		updated.setId(existing.getId());
		appConfigDao.save(updated);

		return ApiResponse.success(null, "AppConfig updated successfully");
	}

	@Override
	public ApiResponse<Void> deleteAppConfig(String keyName) {
		AppConfig existing = appConfigDao.findByKeyName(keyName)
				.orElseThrow(() -> new ResourceNotFoundException("AppConfig with keyName '" + keyName + "' not found"));

		appConfigDao.delete(existing);
		return ApiResponse.success(null, "AppConfig deleted successfully");
	}
}
//END 