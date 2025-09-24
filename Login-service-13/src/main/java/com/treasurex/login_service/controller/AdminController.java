package com.treasurex.login_service.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.treasurex.login_service.dto.ApprovelRequest;
import com.treasurex.login_service.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	// Test end point to check if controller is reachable
	@GetMapping("/test")
	public ResponseEntity<Map<String, String>> test() {
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "Admin test is reached"));
	}

	@PostMapping("/approve")
	public ResponseEntity<Map<String, String>> Approve(@Valid @RequestBody ApprovelRequest approvelRequest) {
		String result = adminService.approve(approvelRequest);
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", result));
	}
}
