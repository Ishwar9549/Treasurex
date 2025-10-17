package com.treasurex.login_service.featureflag;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByName(String name);
}
