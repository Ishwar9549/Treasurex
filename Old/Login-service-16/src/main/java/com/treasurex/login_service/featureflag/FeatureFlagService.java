package com.treasurex.login_service.featureflag;

import java.util.List;
import org.springframework.stereotype.Service;

import com.treasurex.login_service.featureflag.dto.FeatureFlagRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

	private final FeatureFlagRepository repository;

	public String addFeatureFlag(FeatureFlagRequest request) {
		repository.save(FeatureFlag.builder().name(request.getName()).enabled(request.getEnabled())
				.description(request.getDescription()).build());
		return "New Feature Flag added..";

	}

	public List<FeatureFlag> getAllFlags() {
		return repository.findAll();
	}

	public boolean isFeatureEnabled(String name) {
		return repository.findByName(name).map(FeatureFlag::isEnabled).orElse(false); // default false if not found
	}

	public FeatureFlag toggleFlag(String name, boolean enabled) {
		FeatureFlag flag = repository.findByName(name)
				.orElse(FeatureFlag.builder().name(name).description("Auto-created flag").build());
		flag.setEnabled(enabled);
		return repository.save(flag);
	}
}
