package com.treasurex.feature_flag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.treasurex.feature_flag.dto.ApiResponse;
import com.treasurex.feature_flag.dto.FeatureFlagMapper;
import com.treasurex.feature_flag.dto.FeatureFlagRequest;
import com.treasurex.feature_flag.entity.FeatureFlag;
import com.treasurex.feature_flag.exception.ResourceConflictException;
import com.treasurex.feature_flag.exception.ResourceNotFoundException;
import com.treasurex.feature_flag.repository.FeatureFlagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeatureFlagServiceImpl implements FeatureFlagService {

	private final FeatureFlagRepository featureFlagRepository;
	private final FeatureFlagMapper featureFlagMapper;

	@Override
	public ApiResponse<Void> createFeatureFlag(FeatureFlagRequest request) {
		// Check duplicate by name
		if (featureFlagRepository.findByName(request.getName()).isPresent()) {
			throw new ResourceConflictException("FeatureFlag with name '" + request.getName() + "' already exists");
		}

		FeatureFlag entity = featureFlagMapper.featureFlagRequestToEntity(request);
		featureFlagRepository.save(entity);
		return ApiResponse.success(null, "FeatureFlag created successfully");
	}

	@Override
	public ApiResponse<List<FeatureFlag>> getAllFeatureFlags() {
		List<FeatureFlag> flags = featureFlagRepository.findAll();
		return ApiResponse.success(flags, "All FeatureFlags retrieved");
	}

	@Override
	public ApiResponse<FeatureFlag> getFeatureFlagById(Long id) {
		FeatureFlag flag = featureFlagRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("FeatureFlag with ID '" + id + "' not found"));
		return ApiResponse.success(flag, "FeatureFlag retrieved");
	}

	@Override
	public ApiResponse<FeatureFlag> getFeatureFlagByName(String name) {
		FeatureFlag flag = featureFlagRepository.findByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("FeatureFlag with name '" + name + "' not found"));
		return ApiResponse.success(flag, "FeatureFlag retrieved");
	}

	@Override
	public ApiResponse<Void> updateFeatureFlag(FeatureFlagRequest request) {
		FeatureFlag existing = featureFlagRepository.findByName(request.getName()).orElseThrow(
				() -> new ResourceNotFoundException("FeatureFlag with name '" + request.getName() + "' not found"));

		FeatureFlag updated = featureFlagMapper.featureFlagRequestToEntity(request);
		updated.setId(existing.getId());
		featureFlagRepository.save(updated);

		return ApiResponse.success(null, "FeatureFlag updated successfully");
	}

	@Override
	public ApiResponse<Void> deleteFeatureFlag(String name) {
		FeatureFlag existing = featureFlagRepository.findByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("FeatureFlag with name '" + name + "' not found"));

		featureFlagRepository.deleteById(existing.getId());
		return ApiResponse.success(null, "FeatureFlag deleted successfully");
	}

	@Override
	public ApiResponse<FeatureFlag> toggleFeatureFlagByName(String name) {
		FeatureFlag existing = featureFlagRepository.findByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("FeatureFlag with name '" + name + "' not found"));

		existing.setEnabled(!existing.isEnabled());
		featureFlagRepository.save(existing);

		return ApiResponse.success(existing, "FeatureFlag toggled successfully");
	}
}
