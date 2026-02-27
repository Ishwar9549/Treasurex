package com.treasurex.payment_gateway.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.treasurex.payment_gateway.dto.ApiResponse;
import com.treasurex.payment_gateway.dto.PaymentOrderRequest;
import com.treasurex.payment_gateway.model.UserOrders;
import com.treasurex.payment_gateway.service.UserOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserOrdersController {

	private final UserOrderService userOrderService;

	/**
	 * Test endpoint to verify controller availability
	 */
	@GetMapping("/test")
	@ResponseBody
	public ResponseEntity<ApiResponse<Void>> test() {
		log.info("Test endpoint called");
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, "Public test request completed"));
	}

	/**
	 * Load subscription page
	 */
	@GetMapping("/")
	public String init() {
		log.info("Loading subscription page");
		return "index";
	}

	/**
	 * Payment success page
	 */
	@GetMapping("/success")
	public String success() {
		log.info("Redirected to SUCCESS page");
		return "success";
	}

	/**
	 * Payment failure page
	 */
	@GetMapping("/fail")
	public String fail() {
		log.info("Redirected to FAILURE page");
		return "fail";
	}

	/**
	 * Create Razorpay order
	 */
	@ResponseBody
	@PostMapping("/create-order")
	public ResponseEntity<UserOrders> createOrder(@Valid @RequestBody PaymentOrderRequest request) throws Exception {

		log.info("Create order request received for email: {}", request.getEmail());

		UserOrders createdOrder = userOrderService.createOrder(request);

		log.info("Order created successfully with Razorpay Order ID: {}", createdOrder.getRazorpayOrderId());

		return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
	}

	/**
	 * Razorpay payment callback handler This endpoint is called only on successful
	 * payment
	 */
	@PostMapping("/handle-payment-callback")
	public String handlePaymentCallback(@RequestParam Map<String, String> respPayLoad) {

		log.info("Razorpay callback received");
		log.info("---- Razorpay Callback Payload START ----");

		// Print everything received from Razorpay
		respPayLoad.forEach((key, value) -> log.info("{} : {}", key, value));

		log.info("---- Razorpay Callback Payload END ----");

		// SUCCESS CASE (payment completed)
		if (respPayLoad.containsKey("razorpay_payment_id")) {
			log.info("Payment SUCCESS detected");
			userOrderService.updateOrder(respPayLoad);
			return "redirect:/success";
		}
		// FAILURE / INCOMPLETE CASE
		else {
			log.warn("Payment FAILURE or INCOMPLETE detected");
			userOrderService.updateOrder(respPayLoad);
			return "redirect:/fail";
		}
	}
}
