package com.treasurex.payment_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.treasurex.payment_gateway.service.PaymentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller for Payment Gateway Operations
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Gateway API", description = "Manage payment orders and verification")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@GetMapping("/createOrder")
	public String createOrder(@RequestParam("amount") int amount, @RequestParam("currency") String currency) {
		try {
			return paymentService.createOrder(amount, currency);
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
