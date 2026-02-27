package com.treasurex.feature_flag.dto;

import org.springframework.stereotype.Component;

import com.treasurex.feature_flag.model.FeatureFlag;

@Component
public class FeatureFlagConverter {

	/*
	 * Mapping Feature Flag Request to Feature Flag Entity
	 */
	public FeatureFlag featureFlagRequestToEntity(FeatureFlagRequest request) {
		FeatureFlag featureFlag = FeatureFlag.builder().name(request.getName())
				.enabled(request.getEnabled() != null ? request.getEnabled() : false)
				.description(request.getDescription()).build();
		return featureFlag;
	}
}
//END
