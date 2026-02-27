package com.treasurex.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.userservice.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/private")
@RequiredArgsConstructor
public class PrivateController {

	/**
	 * Test end point to check if controller is reachable and for this end point
	 * need authentication
	 */
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Private test request completed"));
	}
}
//END
