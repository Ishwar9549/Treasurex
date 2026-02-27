package com.treasurex.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.userservice.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Controller for authenticated-only test endpoints. Used to verify security
 * configuration and access control. All endpoints under this controller require
 * authentication.
 */
@RestController
@RequestMapping("/private")
public class PrivateController {

	@Operation(summary = "Verify authenticated access to private endpoints")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {
		return ResponseEntity.ok(ApiResponse.success(null, "Authenticated access successful"));
	}
}
//END