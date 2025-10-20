package com.treasurex.feature_flag.dto;

import org.springframework.stereotype.Component;
import com.treasurex.feature_flag.entity.FeatureFlag;
import lombok.RequiredArgsConstructor;

/**
 * Mapper class to convert DTOs into Entities.
 */
@Component
@RequiredArgsConstructor
public class FeatureFlagMapper {

	public FeatureFlag featureFlagRequestToEntity(FeatureFlagRequest request) {
		return FeatureFlag.builder().name(request.getName()).enabled(request.getEnabled())
				.description(request.getDescription()).build();
	}
}
