/*package com.treasurex.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.treasurex.user_service.dto.ApiResponse;

// Feign client pointing to your feature flag service
@FeignClient(name = "feature-flag-client", url = "${feature.flag.url:http://localhost:9090}")
public interface FeatureFlagFeignClient {

	@GetMapping("/feature-flag/is-feature-enabled")
	ApiResponse isFeatureEnabled(@RequestParam("name") String name);
}
*/