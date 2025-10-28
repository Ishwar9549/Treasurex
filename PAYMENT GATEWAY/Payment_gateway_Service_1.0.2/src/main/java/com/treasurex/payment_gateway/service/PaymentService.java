package com.treasurex.payment_gateway.service;

import java.util.List;

import com.razorpay.RazorpayException;
import com.treasurex.payment_gateway.dto.ApiResponse;
import com.treasurex.payment_gateway.dto.CreateOrderRequest;
import com.treasurex.payment_gateway.dto.OrderResponse;
import com.treasurex.payment_gateway.dto.PaymentVerificationRequest;
import com.treasurex.payment_gateway.entity.PaymentTransaction;

public interface PaymentService {

	ApiResponse<OrderResponse> createOrder(CreateOrderRequest request) throws RazorpayException;

	ApiResponse<PaymentTransaction> getTransactionByOrderId(String orderId);

	ApiResponse<List<PaymentTransaction>> getTransactionsByUser(Long userId);

	ApiResponse<String> verifyPayment(PaymentVerificationRequest request) throws Exception;

}
