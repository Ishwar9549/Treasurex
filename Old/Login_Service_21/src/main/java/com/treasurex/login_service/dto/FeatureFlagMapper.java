package com.treasurex.login_service.dto;

import org.springframework.stereotype.Component;

import com.treasurex.login_service.entity.FeatureFlag;

@Component
public class FeatureFlagMapper {

	/*
	 * Mapping Feature Flag Request to Feature Flag Entity
	 */
	public FeatureFlag featureFlagRequestToEntity(FeatureFlagRequest reequest) {
		FeatureFlag featureFlag = FeatureFlag.builder().name(reequest.getName()).enabled(reequest.getEnabled())
				.description(reequest.getDescription()).build();
		return featureFlag;
	}
}
//END
