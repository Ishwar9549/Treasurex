package com.treasurex.payment_gateway.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.treasurex.payment_gateway.dto.ApiResponse;
import com.treasurex.payment_gateway.dto.CreateOrderRequest;
import com.treasurex.payment_gateway.dto.OrderResponse;
import com.treasurex.payment_gateway.dto.PaymentVerificationRequest;
import com.treasurex.payment_gateway.entity.PaymentTransaction;
import com.treasurex.payment_gateway.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

/**
 * Controller for Payment Gateway Operations
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
 
	private final PaymentService paymentService;

	// ---------------- Test End point ----------------
	@Operation(summary = "Test endpoint to verify Payment Gateway controller is reachable")
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test() {
		return ResponseEntity.ok(ApiResponse.success(null, "Payment Gateway Controller test endpoint reached"));
	}

	@PostMapping("/createOrder")
	public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
		try {
			return paymentService.createOrder(request); // Pass the DTO directly
		} catch (Exception e) {
			return ApiResponse.error(500, e.getMessage());
		}
	}

	@PostMapping("/verifyPayment")
	public ApiResponse<String> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
		try {
			return paymentService.verifyPayment(request); // Service returns ApiResponse<String>
		} catch (Exception e) {
			return ApiResponse.error(500, e.getMessage());
		}
	}

	@GetMapping("/user/{userId}")
	public ApiResponse<List<PaymentTransaction>> getUserTransactionsByID(@PathVariable("userId") Long userId) {
		try {
			return paymentService.getTransactionsByUser(userId); // Service returns ApiResponse<List<>>
		} catch (Exception e) {
			return ApiResponse.error(500, e.getMessage());
		}
	}
}
