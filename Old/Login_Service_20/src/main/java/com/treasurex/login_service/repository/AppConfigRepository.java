package com.treasurex.login_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.login_service.entity.AppConfig;

/**
 * Repository for managing AppConfig entities.
 */
public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {

	Optional<AppConfig> findByKeyName(String keyName);
}
//END
