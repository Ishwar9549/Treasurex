package com.treasurex.feature_flag.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.treasurex.feature_flag.entity.FeatureFlag;

/**
 * Repository for managing FeatureFlag entities.
 */
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
	Optional<FeatureFlag> findByName(String name);
}
