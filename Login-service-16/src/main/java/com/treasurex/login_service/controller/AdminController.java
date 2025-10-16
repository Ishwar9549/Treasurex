package com.treasurex.login_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.treasurex.login_service.dto.ApiResponse;
import com.treasurex.login_service.dto.ApprovalRequest;
import com.treasurex.login_service.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin API", description = "Admin controller")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	/**
	 * Test end point to check if controller is reachable
	 */
	@Operation(summary = "Test end point to check if Admin controller is reachable", description = "")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse> test() {
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message("Admin test is reached").build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	/**
	 * End point to approve requests Business & Advisor accounts require ADMIN
	 * approval ADMIN can approve/reject users.
	 */
	@Operation(summary = "End point to approve requests. Business & Advisor accounts require Admin approval Admin can approve/reject users", description = "")
	@PostMapping("/approve")
	public ResponseEntity<ApiResponse> approve(@Valid @RequestBody ApprovalRequest approvelRequest) {
		String result = adminService.approve(approvelRequest);
		ApiResponse apiResponse = ApiResponse.builder().status("SUCCESS").message(result).build();
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}
}
