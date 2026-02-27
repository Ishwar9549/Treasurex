package com.treasurex.feature_flag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.treasurex.feature_flag.dto.ApiResponse;
import com.treasurex.feature_flag.dto.FeatureFlagConverter;
import com.treasurex.feature_flag.dto.FeatureFlagRequest;
import com.treasurex.feature_flag.exception.ResourceConflictException;
import com.treasurex.feature_flag.exception.ResourceNotFoundException;
import com.treasurex.feature_flag.mapper.FeatureFlagMapper;
import com.treasurex.feature_flag.model.FeatureFlag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for FeatureFlag operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureFlagServiceImpl implements FeatureFlagService {

	private final FeatureFlagMapper featureFlagMapper;
	private final FeatureFlagConverter featureFlagConverter;

	/**
	 * Create a new FeatureFlag.
	 */
	@Override
	public ApiResponse<Void> createFeatureFlag(FeatureFlagRequest request) {

		log.debug("Creating FeatureFlag with name={}", request.getName());

		FeatureFlag existing = featureFlagMapper.findByName(request.getName());
		if (existing != null) {
			log.warn("FeatureFlag already exists with name={}", request.getName());
			throw new ResourceConflictException("FeatureFlag with name '" + request.getName() + "' already exists");
		}

		FeatureFlag entity = featureFlagConverter.featureFlagRequestToEntity(request);
		featureFlagMapper.save(entity);

		log.info("FeatureFlag created successfully with name={}", request.getName());
		return ApiResponse.success(null, "FeatureFlag created successfully");
	}

	/**
	 * Retrieve all FeatureFlags.
	 */
	@Override
	public ApiResponse<List<FeatureFlag>> getAllFeatureFlags() {

		log.debug("Fetching all FeatureFlags");
		List<FeatureFlag> flags = featureFlagMapper.findAll();

		return ApiResponse.success(flags, "All FeatureFlags retrieved");
	}

	/**
	 * Retrieve FeatureFlag by name.
	 */
	@Override
	public ApiResponse<FeatureFlag> getFeatureFlagByName(String name) {

		log.debug("Fetching FeatureFlag with name={}", name);

		FeatureFlag flag = featureFlagMapper.findByName(name);
		if (flag == null) {
			log.warn("FeatureFlag not found with name={}", name);
			throw new ResourceNotFoundException("FeatureFlag with name '" + name + "' not found");
		}

		return ApiResponse.success(flag, "FeatureFlag retrieved");
	}

	/**
	 * Update an existing FeatureFlag.
	 */
	@Override
	public ApiResponse<Void> updateFeatureFlag(FeatureFlagRequest request) {

		log.debug("Updating FeatureFlag with name={}", request.getName());

		FeatureFlag existing = featureFlagMapper.findByName(request.getName());
		if (existing == null) {
			log.warn("Cannot update. FeatureFlag not found with name={}", request.getName());
			throw new ResourceNotFoundException("FeatureFlag with name '" + request.getName() + "' not found");
		}

		FeatureFlag updated = featureFlagConverter.featureFlagRequestToEntity(request);
		updated.setId(existing.getId());

		featureFlagMapper.update(updated);

		log.info("FeatureFlag updated successfully with name={}", request.getName());
		return ApiResponse.success(null, "FeatureFlag updated successfully");
	}

	/**
	 * Delete FeatureFlag by name.
	 */
	@Override
	public ApiResponse<Void> deleteFeatureFlag(String name) {

		log.debug("Deleting FeatureFlag with name={}", name);

		FeatureFlag existing = featureFlagMapper.findByName(name);
		if (existing == null) {
			log.warn("Cannot delete. FeatureFlag not found with name={}", name);
			throw new ResourceNotFoundException("FeatureFlag with name '" + name + "' not found");
		}

		featureFlagMapper.deleteById(existing.getId());

		log.info("FeatureFlag deleted successfully with name={}", name);
		return ApiResponse.success(null, "FeatureFlag deleted successfully");
	}

	/**
	 * Toggle FeatureFlag enabled/disabled state.
	 */
	@Override
	public ApiResponse<FeatureFlag> toggleFeatureFlagByName(String name) {

		log.debug("Toggling FeatureFlag with name={}", name);

		FeatureFlag existing = featureFlagMapper.findByName(name);
		if (existing == null) {
			log.warn("Cannot toggle. FeatureFlag not found with name={}", name);
			throw new ResourceNotFoundException("FeatureFlag with name '" + name + "' not found");
		}

		existing.setEnabled(!existing.getEnabled());
		featureFlagMapper.update(existing);

		log.info("FeatureFlag toggled successfully with name={}, enabled={}", name, existing.getEnabled());

		return ApiResponse.success(existing, "FeatureFlag toggled successfully");
	}

	/**
	 * Check whether a FeatureFlag is enabled.
	 */
	@Override
	public ApiResponse<Void> isFeatureEnabled(String name) {

		log.debug("Checking FeatureFlag status for name={}", name);

		FeatureFlag featureFlag = featureFlagMapper.findByName(name);
		if (featureFlag == null) {
			log.warn("FeatureFlag not found while checking status. name={}", name);
			throw new ResourceNotFoundException(name + " not found");
		}

		if (featureFlag.getEnabled()) {
			return ApiResponse.success(null, "Feature flag is enabled..");
		}

		log.info("FeatureFlag is disabled. name={}", name);
		return ApiResponse.error(403, "Feature flag is disabled..");
	}
}
