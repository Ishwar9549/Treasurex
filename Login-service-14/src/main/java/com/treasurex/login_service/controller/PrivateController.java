package com.treasurex.login_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.treasurex.login_service.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Private API", description = "for this need authentication JSON")
@RestController
@RequestMapping("/private")
public class PrivateController {

	/**
	 * Test end point to check if controller is reachable for need authentication
	 */
	@Operation(summary = "Test end point to check if private controller is reachable or not for this need authentication", description = "")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse> test() {
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Private test is reached").build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}
}