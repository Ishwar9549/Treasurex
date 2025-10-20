package com.treasurex.app_config.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.treasurex.app_config.dto.ApiResponse;
import com.treasurex.app_config.dto.AppConfigRequest;
import com.treasurex.app_config.entity.AppConfig;
import com.treasurex.app_config.service.AppConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "App Config API", description = "Manage global and microservice-level app configurations dynamically")
@RestController
@RequestMapping("/app-config")
@RequiredArgsConstructor
public class AppConfigController {

	private final AppConfigService appConfigService;

	// ---------------- Test End point ----------------
	@Operation(summary = "app-config Controller Test endpoint to check if controller is reachable")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse> test() {
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("app-config test is reached").build();
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping("/add-or-update")
	@Operation(summary = "Add or Update App Config", description = "Adds a new configuration or updates existing one dynamically")
	public ResponseEntity<ApiResponse> addOrUpdateConfig(@Valid @RequestBody AppConfigRequest request) {
		String message = appConfigService.addOrUpdateConfig(request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(message).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	@GetMapping("/get/{key}")
	@Operation(summary = "Get Config by Key", description = "Fetch configuration by key name")
	public ResponseEntity<ApiResponse> getConfig(@PathVariable("key") String key) {
		System.err.println("ok...");
		AppConfig config = appConfigService.getConfigByKey(key);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Config fetched successfully")
				.data(Map.of("config", config)).build();
		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/get-all")
	@Operation(summary = "Get All Configs", description = "Fetch all stored application configurations")
	public ResponseEntity<ApiResponse> getAllConfigs() {
		List<AppConfig> configs = appConfigService.getAllConfigs();
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Configs fetched successfully")
				.data(Map.of("configs", configs)).build();
		return ResponseEntity.ok(apiResponse);
	}

	@DeleteMapping("/delete/{key}")
	@Operation(summary = "Delete Config", description = "Delete a specific configuration by key name")
	public ResponseEntity<ApiResponse> deleteConfig(@PathVariable("key") String key) {
		boolean deleted = appConfigService.deleteConfig(key);
		ApiResponse apiResponse = ApiResponse.builder().status(deleted ? "SUCCESS" : "FAILED")
				.message(deleted ? "Config deleted successfully" : "Config not found").build();
		return ResponseEntity.ok(apiResponse);
	}
}
