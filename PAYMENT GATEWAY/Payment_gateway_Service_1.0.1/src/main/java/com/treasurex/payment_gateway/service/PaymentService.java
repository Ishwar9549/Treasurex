package com.treasurex.payment_gateway.service;

import com.razorpay.RazorpayException;

/**
 * Payment Service interface for managing payment orders and verification
 */
public interface PaymentService {

	String createOrder(int amount, String currency) throws RazorpayException;

}
