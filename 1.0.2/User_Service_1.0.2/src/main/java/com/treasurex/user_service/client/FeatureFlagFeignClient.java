package com.treasurex.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.treasurex.user_service.dto.ApiResponse;

// Feign client pointing to your feature flag service

@FeignClient(name = "feature-flag-client", url = "${feature.flag.url:http://localhost:9091}")
public interface FeatureFlagFeignClient {

	@GetMapping("/feature_flags/is-feature-enabled/{name}")
	ApiResponse<Void> isFeatureEnabled(@PathVariable("name") String name);
}
 