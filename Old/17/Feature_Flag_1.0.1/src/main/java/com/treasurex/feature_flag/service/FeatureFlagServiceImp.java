package com.treasurex.feature_flag.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.treasurex.feature_flag.dto.FeatureFlagByNameRequest;
import com.treasurex.feature_flag.dto.FeatureFlagMapper;
import com.treasurex.feature_flag.dto.FeatureFlagRequest;
import com.treasurex.feature_flag.dto.ToggleFeatureFlagRequest;
import com.treasurex.feature_flag.entity.FeatureFlag;
import com.treasurex.feature_flag.exception.InvalidCredentialsException;
import com.treasurex.feature_flag.exception.ResourceNotFoundException;
import com.treasurex.feature_flag.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeatureFlagServiceImp implements FeatureFlagService {

	private final FeatureFlagRepository repository;
	private final FeatureFlagMapper featureFlagMapper;

	@Override
	public String addFeatureFlag(FeatureFlagRequest request) {

		if (repository.findByName(request.getName()).isPresent()) {
			throw new InvalidCredentialsException(request.getName() + " Feauture flag already in use: ");
		}
		repository.save(featureFlagMapper.featureFlagRequestToEntity(request));
		return request.getName() + " Feature Added...";
	}

	@Override
	public List<FeatureFlag> getAllFeatureFlags() {
		List<FeatureFlag> result = repository.findAll();
		return result;
	}

	@Override
	public FeatureFlag getFeatureFlagByName(FeatureFlagByNameRequest request) {
		return repository.findByName(request.getName())
				.orElseThrow(() -> new ResourceNotFoundException(request.getName() + " not found "));
	}

	@Override
	public FeatureFlag toggleFeatureFlag(ToggleFeatureFlagRequest request) {

		FeatureFlag featureFlag = repository.findByName(request.getName())
				.orElseThrow(() -> new ResourceNotFoundException(request.getName() + " not found "));

		if (featureFlag.isEnabled()) {
			featureFlag.setEnabled(false);
		} else {
			featureFlag.setEnabled(true);
		}
		repository.save(featureFlag);
		return featureFlag;
	}

	@Override
	public boolean isFeatureEnabled(String request) {
		FeatureFlag featureFlag = repository.findByName(request)
				.orElseThrow(() -> new ResourceNotFoundException(request + " not found "));
		if (featureFlag.isEnabled()) {
			return true;
		} else {
			return false;
		}
	}
}
