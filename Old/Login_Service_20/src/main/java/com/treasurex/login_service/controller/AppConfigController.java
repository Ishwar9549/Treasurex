package com.treasurex.login_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.login_service.dto.ApiResponse;
import com.treasurex.login_service.dto.AppConfigRequest;
import com.treasurex.login_service.entity.AppConfig;
import com.treasurex.login_service.service.AppConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for managing AppConfig. Provides CRUD end points with descriptive
 * URLs.
 */
@Tag(name = "App Config API", description = "Manage dynamic application configurations")
@RestController
@RequestMapping("/app-config")
@RequiredArgsConstructor
public class AppConfigController {

	private final AppConfigService appConfigService;

	// ---------------- Test End point ----------------
	@Operation(summary = "Test endpoint to verify AppConfig controller is reachable")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {
		return ResponseEntity.ok(ApiResponse.success(null, "AppConfig Controller test endpoint reached"));
	}

	// ---------------- Create ----------------
	@Operation(summary = "Create a new AppConfig")
	@PostMapping("/add")
	public ResponseEntity<ApiResponse<Void>> addAppConfig(@Valid @RequestBody AppConfigRequest request) {
		return ResponseEntity.status(201).body(appConfigService.createAppConfig(request));
	}

	// ---------------- Read All ----------------
	@Operation(summary = "Retrieve all AppConfigs")
	@GetMapping("/get_all")
	public ResponseEntity<ApiResponse<List<AppConfig>>> getAllAppConfigs() {
		return ResponseEntity.ok(appConfigService.getAllAppConfig());
	}

	// ---------------- Read by Name ----------------
	@Operation(summary = "Retrieve AppConfig by keyName")
	@GetMapping("/get_by_name/{keyName}")
	public ResponseEntity<ApiResponse<AppConfig>> getAppConfigByName(@PathVariable("keyName") String keyName) {
		return ResponseEntity.ok(appConfigService.getAppConfigByName(keyName));
	}

	// ---------------- Update ----------------
	@Operation(summary = "Update an existing AppConfig")
	@PutMapping("/update")
	public ResponseEntity<ApiResponse<Void>> updateAppConfig(@Valid @RequestBody AppConfigRequest request) {
		return ResponseEntity.ok(appConfigService.updateAppConfig(request));
	}

	// ---------------- Delete ----------------
	@Operation(summary = "Delete AppConfig by keyName")
	@DeleteMapping("/delete/{keyName}")
	public ResponseEntity<ApiResponse<Void>> deleteAppConfig(@PathVariable("keyName") String keyName) {
		return ResponseEntity.ok(appConfigService.deleteAppConfig(keyName));
	}
}
//END 