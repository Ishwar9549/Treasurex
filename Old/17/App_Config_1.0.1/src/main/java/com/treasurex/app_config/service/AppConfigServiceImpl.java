package com.treasurex.app_config.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.treasurex.app_config.dto.AppConfigRequest;
import com.treasurex.app_config.entity.AppConfig;
import com.treasurex.app_config.repository.AppConfigRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppConfigServiceImpl implements AppConfigService {

	private final AppConfigRepository appConfigRepository;

	@Override
	public String addOrUpdateConfig(AppConfigRequest request) {
		AppConfig config = appConfigRepository.findByKeyName(request.getKeyName())
				.orElse(AppConfig.builder().keyName(request.getKeyName()).build());
		config.setValue(request.getValue());
		config.setDescription(request.getDescription());
		config.setType(request.getType());
		appConfigRepository.save(config);
		return "Configuration saved successfully";
	}

	@Override
	public AppConfig getConfigByKey(String key) {
		return appConfigRepository.findByKeyName(key)
				.orElseThrow(() -> new RuntimeException("Configuration not found: " + key));
	}

	@Override
	public List<AppConfig> getAllConfigs() {
		return appConfigRepository.findAll();
	}

	@Override
	public boolean deleteConfig(String key) {
		return appConfigRepository.findByKeyName(key).map(cfg -> {
			appConfigRepository.delete(cfg);
			return true;
		}).orElse(false);
	}
}
