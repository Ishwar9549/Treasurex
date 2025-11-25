package com.treasurex.login_service.dao;

import java.util.List;
import java.util.Optional;

import com.treasurex.login_service.entity.AppConfig;

public interface AppConfigDao {

	void save(AppConfig appConfig);

	void update(AppConfig appConfig);

	void delete(AppConfig appConfig);

	List<AppConfig> findAll();

	Optional<AppConfig> findById(Long id);

	Optional<AppConfig> findByKeyName(String keyName);
}
