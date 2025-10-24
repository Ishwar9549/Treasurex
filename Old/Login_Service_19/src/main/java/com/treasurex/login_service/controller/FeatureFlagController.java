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
import com.treasurex.login_service.dto.FeatureFlagRequest;
import com.treasurex.login_service.entity.FeatureFlag;
import com.treasurex.login_service.service.FeatureFlagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feature_flags")
@RequiredArgsConstructor
@Tag(name = "Feature Flag API", description = "Manage feature flags dynamically")
public class FeatureFlagController {

	private final FeatureFlagService featureFlagService;

	// ---------------- Test End point ----------------
	@Operation(summary = "Test endpoint to verify feature flag controller is reachable")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {
		return ResponseEntity.ok(ApiResponse.success(null, "feature flag  Controller test endpoint reached"));
	}

	// ---------------- Create ----------------
	@Operation(summary = "Create a new FeatureFlag")
	@PostMapping("/add")
	public ResponseEntity<ApiResponse<Void>> addFeatureFlag(@Valid @RequestBody FeatureFlagRequest request) {
		return ResponseEntity.status(201).body(featureFlagService.createFeatureFlag(request));
	}

	// ---------------- Read All ----------------
	@Operation(summary = "Retrieve all FeatureFlags")
	@GetMapping("/get_all")
	public ResponseEntity<ApiResponse<List<FeatureFlag>>> getAllFeatureFlags() {
		return ResponseEntity.ok(featureFlagService.getAllFeatureFlags());
	}

	// ---------------- Read by Name ----------------
	@Operation(summary = "Retrieve FeatureFlag by name")
	@GetMapping("/get_by_name/{name}")
	public ResponseEntity<ApiResponse<FeatureFlag>> getFeatureFlagByName(@PathVariable("name") String name) {
		return ResponseEntity.ok(featureFlagService.getFeatureFlagByName(name));
	}

	// ---------------- Read by ID ----------------
	@Operation(summary = "Retrieve FeatureFlag by ID")
	@GetMapping("/get_by_id/{id}")
	public ResponseEntity<ApiResponse<FeatureFlag>> getFeatureFlagById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(featureFlagService.getFeatureFlagById(id));
	}

	// ---------------- Update ----------------
	@Operation(summary = "Update an existing FeatureFlag")
	@PutMapping("/update")
	public ResponseEntity<ApiResponse<Void>> updateFeatureFlag(@Valid @RequestBody FeatureFlagRequest request) {
		return ResponseEntity.ok(featureFlagService.updateFeatureFlag(request));
	}

	// ---------------- Delete ----------------
	@Operation(summary = "Delete a FeatureFlag by name")
	@DeleteMapping("/delete/{name}")
	public ResponseEntity<ApiResponse<Void>> deleteFeatureFlag(@PathVariable("name") String name) {
		return ResponseEntity.ok(featureFlagService.deleteFeatureFlag(name));
	}

	// ---------------- Toggle ----------------
	@Operation(summary = "Toggle a FeatureFlag by name")
	@PostMapping("/toggle/{name}")
	public ResponseEntity<ApiResponse<FeatureFlag>> toggleFeatureFlag(@PathVariable("name") String name) {
		return ResponseEntity.ok(featureFlagService.toggleFeatureFlagByName(name));
	}

	// GET /feature-flag/is-feature-enabled?name=REGISTRATION_ENABLED
	@Operation(summary = "Check if feature flag is enabled", description = "Returns whether a feature flag is enabled")
	@GetMapping("/is-feature-enabled/{name}")
	public ResponseEntity<ApiResponse<Void>> isFeatureEnabled(@PathVariable("name") String name) {
		return ResponseEntity.ok(featureFlagService.isFeatureEnabled(name));
	}
}
