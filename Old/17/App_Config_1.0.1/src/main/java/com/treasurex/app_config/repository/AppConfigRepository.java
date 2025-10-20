package com.treasurex.app_config.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.treasurex.app_config.entity.AppConfig;

public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {

	Optional<AppConfig> findByKeyName(String keyName);

}
