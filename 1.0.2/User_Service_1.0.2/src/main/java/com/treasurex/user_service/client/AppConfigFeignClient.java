package com.treasurex.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.treasurex.user_service.dto.ApiResponse;
import com.treasurex.user_service.dto.AppConfig;

//Feign client pointing to your AppConfig service

@FeignClient(name = "AppConfig-client", url = "${app.config.url:http://localhost:9092}")
public interface AppConfigFeignClient {

	@GetMapping("app-config/get_by_name/{keyName}")
	ApiResponse<AppConfig> getAppConfigByName(@PathVariable("keyName") String keyName);
}
