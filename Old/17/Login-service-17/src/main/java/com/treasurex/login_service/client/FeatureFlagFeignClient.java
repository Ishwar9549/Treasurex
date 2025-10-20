package com.treasurex.login_service.client;

import com.treasurex.login_service.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Feign client pointing to your feature flag service
@FeignClient(name = "feature-flag-client", url = "${feature.flag.url:http://localhost:9091}")
public interface FeatureFlagFeignClient {

	@GetMapping("/feature-flag/is-feature-enabled")
	ApiResponse isFeatureEnabled(@RequestParam("name") String name);
}
