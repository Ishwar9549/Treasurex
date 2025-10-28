package com.treasurex.payment_gateway.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.treasurex.payment_gateway.dto.ApiResponse;
import com.treasurex.payment_gateway.dto.CreateOrderRequest;
import com.treasurex.payment_gateway.dto.OrderResponse;
import com.treasurex.payment_gateway.dto.PaymentVerificationRequest;
import com.treasurex.payment_gateway.entity.PaymentTransaction;
import com.treasurex.payment_gateway.repository.PaymentTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final RazorpayClient razorpayClient;
	private final PaymentTransactionRepository paymentTransactionRepository;

	@Override
	public ApiResponse<OrderResponse> createOrder(CreateOrderRequest request) throws RazorpayException {

		// Prepare Razorpay order request
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", request.getAmount() * 100); // convert to paise
		orderRequest.put("currency", request.getCurrency());
		orderRequest.put("payment_capture", 1);

		Order order = razorpayClient.orders.create(orderRequest);

		// Save transaction to DB
		PaymentTransaction tx = PaymentTransaction.builder().orderId(order.get("id")).userId(request.getUserId())
				.amount(request.getAmount() * 1.0).currency(request.getCurrency()).status("CREATED").build();

		paymentTransactionRepository.save(tx);

		// Return structured response
		OrderResponse response = OrderResponse.builder().orderId(order.get("id")).amount(order.get("amount"))
				.currency(order.get("currency")).status("CREATED").build();

		return ApiResponse.success(response, "Order created successfully");
	}

	@Override
	public ApiResponse<PaymentTransaction> getTransactionByOrderId(String orderId) {
		PaymentTransaction tx = paymentTransactionRepository.findByOrderId(orderId)
				.orElseThrow(() -> new RuntimeException("Transaction not found"));
		return ApiResponse.success(tx, "Transaction fetched");
	}

	@Override
	public ApiResponse<List<PaymentTransaction>> getTransactionsByUser(Long userId) {
		List<PaymentTransaction> list = paymentTransactionRepository.findByUserId(userId);
		return ApiResponse.success(list, "User transactions fetched");
	}

	@Override
	public ApiResponse<String> verifyPayment(PaymentVerificationRequest request) throws Exception {

		// Create signature string
		String payload = request.getOrderId() + "|" + request.getPaymentId();

		// Compute HMAC SHA256 using Razorpay Utils
		String generatedSignature = Utils.getHash(payload, "9EzQmbYq1zSTy3UYJtQ1juW2");

		// Compare signatures
		boolean valid = generatedSignature.equals(request.getSignature());

		if (valid) {
			PaymentTransaction tx = paymentTransactionRepository.findByOrderId(request.getOrderId())
					.orElseThrow(() -> new RuntimeException("Transaction not found"));
			tx.setStatus("PAID");
			tx.setPaymentId(request.getPaymentId());
			paymentTransactionRepository.save(tx);

			return ApiResponse.success("Payment verified successfully", "Payment verified successfully");
		} else {
			return ApiResponse.error(400, "Payment verification failed");
		}
	}
}
