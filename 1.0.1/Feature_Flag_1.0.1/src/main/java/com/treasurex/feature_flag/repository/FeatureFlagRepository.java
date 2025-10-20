package com.treasurex.feature_flag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.feature_flag.entity.FeatureFlag;

/**
 * Repository for managing FeatureFlag entities.
 */
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {

	// Find a feature by its unique Id
	Optional<FeatureFlag> findById(Long id);

	// Find a feature by its unique name
	Optional<FeatureFlag> findByName(String name);

}
