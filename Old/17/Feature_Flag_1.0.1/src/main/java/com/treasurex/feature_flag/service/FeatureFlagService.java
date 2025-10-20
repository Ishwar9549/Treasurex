package com.treasurex.feature_flag.service;

import java.util.List;

import com.treasurex.feature_flag.dto.FeatureFlagByNameRequest;
import com.treasurex.feature_flag.dto.FeatureFlagRequest;
import com.treasurex.feature_flag.dto.ToggleFeatureFlagRequest;
import com.treasurex.feature_flag.entity.FeatureFlag;

public interface FeatureFlagService {

	/**
	 * Create or add a new feature flag
	 */
	String addFeatureFlag(FeatureFlagRequest request);

	/**
	 * Get all feature flags
	 */
	List<FeatureFlag> getAllFeatureFlags();

	/**
	 * Get a specific feature flag by its name
	 */
	FeatureFlag getFeatureFlagByName(FeatureFlagByNameRequest request);

	/**
	 * Toggle a feature flag (enable/disable)
	 */
	FeatureFlag toggleFeatureFlag(ToggleFeatureFlagRequest request);

	/**
	 * Check if a feature flag is enabled (used by other services)
	 */
	boolean isFeatureEnabled(String request);
}
