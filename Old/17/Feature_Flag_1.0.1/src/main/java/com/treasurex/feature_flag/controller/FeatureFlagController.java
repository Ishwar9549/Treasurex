package com.treasurex.feature_flag.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.treasurex.feature_flag.dto.*;
import com.treasurex.feature_flag.entity.FeatureFlag;
import com.treasurex.feature_flag.service.FeatureFlagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Feature Flag APIs. Provides end points to add, fetch,
 * toggle, and view feature flags.
 */
@Tag(name = "Feature Flag API", description = "APIs for managing feature toggles across TreasureX microservices")
@RestController
@RequestMapping("/feature-flag")
@RequiredArgsConstructor
public class FeatureFlagController {

	private final FeatureFlagService featureFlagService;

	// ---------------- TEST ENDPOINT ----------------
	@Operation(summary = "Health Check", description = "Check if the Feature Flag service is running")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse> test() {
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS")
				.message("Feature Flag Controller is reachable ✅").build();
		return ResponseEntity.ok(apiResponse);
	}

	// ---------------- ADD FEATURE FLAG ----------------
	@Operation(summary = "Add New Feature Flag", description = "Create a new feature flag for toggling a feature")
	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addFeatureFlag(@Valid @RequestBody FeatureFlagRequest request) {
		String result = featureFlagService.addFeatureFlag(request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	// ---------------- GET ALL FEATURE FLAGS ----------------
	@Operation(summary = "Get All Feature Flags", description = "Fetch all existing feature flags with their current status")
	@GetMapping("/get-all")
	public ResponseEntity<ApiResponse> getAllFeatureFlag() {
		List<FeatureFlag> result = featureFlagService.getAllFeatureFlags();
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Feature flags fetched successfully")
				.data(Map.of("featureFlags", result)).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	// ---------------- GET FEATURE FLAG BY NAME ----------------
	@Operation(summary = "Get Feature Flag by Name", description = "Fetch a specific feature flag using its name")
	@GetMapping("/get-by-name")
	public ResponseEntity<ApiResponse> getFeatureFlagByName(@Valid @RequestBody FeatureFlagByNameRequest request) {
		FeatureFlag result = featureFlagService.getFeatureFlagByName(request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Feature flag fetched successfully")
				.data(Map.of("featureFlag", result)).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	// ---------------- TOGGLE FEATURE FLAG ----------------
	@Operation(summary = "Toggle Feature Flag", description = "Enable or disable a specific feature flag")
	@PostMapping("/toggle")
	public ResponseEntity<ApiResponse> toggleFeatureFlag(@Valid @RequestBody ToggleFeatureFlagRequest request) {
		FeatureFlag result = featureFlagService.toggleFeatureFlag(request);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Feature flag toggled successfully")
				.data(Map.of("featureFlag", result)).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	// GET /feature-flag/is-feature-enabled?name=REGISTRATION_ENABLED
	@Operation(summary = "Check if feature flag is enabled", description = "Returns whether a feature flag is enabled")
	@GetMapping("/is-feature-enabled")
	public ResponseEntity<ApiResponse> isFeatureEnabled(@RequestParam("name") String name) {
		boolean result = featureFlagService.isFeatureEnabled(name);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Feature flag is enabled :" + result)
				.data(Map.of("enabled", result)).build();
		return ResponseEntity.ok(apiResponse);
	}
}
