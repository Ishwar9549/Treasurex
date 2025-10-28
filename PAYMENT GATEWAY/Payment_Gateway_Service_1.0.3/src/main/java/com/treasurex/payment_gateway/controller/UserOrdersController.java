package com.treasurex.payment_gateway.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.treasurex.payment_gateway.entity.UserOrders;
import com.treasurex.payment_gateway.service.UserOrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserOrdersController {

	private final UserOrderService userOrderService;

	@GetMapping("/")
	public String init() {
		return "index";
	}

	@PostMapping(value = "/create-order", produces = "application/json")
	@ResponseBody
	public ResponseEntity<UserOrders> createOrder(@RequestBody UserOrders userOrders) throws Exception {
		UserOrders createdOrder = userOrderService.createOrder(userOrders);
		return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
	}

	@PostMapping("/handle-payment-callback")
	public String handlePaymentCallback(@RequestParam Map<String, String> respPayLoad) {
		System.err.println("coiming.." + respPayLoad);
		userOrderService.updateOrder(respPayLoad);
		return "success";
	}
}