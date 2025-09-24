package com.treasurex.login_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private")
public class PrivateController {

	// Test end point to check if controller is reachable
	@GetMapping("/test")
	public ResponseEntity<Map<String, String>> test() {
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "Private test is reached"));
	}
}