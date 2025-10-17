package com.treasurex.login_service.featureflag;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.treasurex.login_service.featureflag.dto.FeatureFlagRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Feature Flag API", description = "Enable or disable features dynamically")
@RestController
@RequestMapping("/feature-flags")
@RequiredArgsConstructor
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;
    
    @PostMapping("/add-flag")
    public String addFeatureFlag(@Valid @RequestBody FeatureFlagRequest request) 
    {
    	String result = featureFlagService.addFeatureFlag(request);
		return result;
    	
    }

    @Operation(summary = "Get all feature flags")
    @GetMapping
    public ResponseEntity<List<FeatureFlag>> getAllFlags() {
        return ResponseEntity.ok(featureFlagService.getAllFlags());
    }

    @Operation(summary = "Check if a specific feature is enabled")
    @GetMapping("/{name}")
    public ResponseEntity<Boolean> isFeatureEnabled(@PathVariable("name") String name) {
        return ResponseEntity.ok(featureFlagService.isFeatureEnabled(name));
    }

    @Operation(summary = "Enable or disable a feature")
    @PostMapping("/{name}/toggle")
    public ResponseEntity<FeatureFlag> toggleFeature(@PathVariable("name") String name, @RequestParam("enabled") boolean enabled) {
        return ResponseEntity.ok(featureFlagService.toggleFlag(name, enabled));
    }
}
