package com.treasurex.login_service.dao;

import java.util.List;
import java.util.Optional;

import com.treasurex.login_service.entity.FeatureFlag;

public interface FeatureFlagDao {

	void save(FeatureFlag featureFlag);

	void update(FeatureFlag featureFlag);

	void delete(FeatureFlag featureFlag);

	List<FeatureFlag> findAll();

	Optional<FeatureFlag> findById(Long id);

	Optional<FeatureFlag> findByName(String name);
}
