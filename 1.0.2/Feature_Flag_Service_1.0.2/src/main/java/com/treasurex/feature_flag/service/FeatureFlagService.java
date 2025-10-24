package com.treasurex.feature_flag.service;

import java.util.List;
import com.treasurex.feature_flag.dto.ApiResponse;
import com.treasurex.feature_flag.dto.FeatureFlagRequest;
import com.treasurex.feature_flag.entity.FeatureFlag;

public interface FeatureFlagService {

	// Create a new feature flag
	ApiResponse<Void> createFeatureFlag(FeatureFlagRequest request);

	// Get all feature flags
	ApiResponse<List<FeatureFlag>> getAllFeatureFlags();

	// Get a feature flag by ID
	ApiResponse<FeatureFlag> getFeatureFlagById(Long id);

	// Get a feature flag by name
	ApiResponse<FeatureFlag> getFeatureFlagByName(String name);

	// Update a feature flag
	ApiResponse<Void> updateFeatureFlag(FeatureFlagRequest request);

	// Delete a feature flag by name
	ApiResponse<Void> deleteFeatureFlag(String name);

	// Toggle feature flag by name (enable/disable)
	ApiResponse<FeatureFlag> toggleFeatureFlagByName(String name);

	// Check if a feature flag is enabled (used by other services)
	ApiResponse<Void> isFeatureEnabled(String request);
}
