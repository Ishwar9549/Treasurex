package com.treasurex.payment_gateway.service;

import java.util.Map;

import com.treasurex.payment_gateway.dto.PaymentOrderRequest;
import com.treasurex.payment_gateway.model.UserOrders;

/**
 * Service interface for handling user subscription orders
 * and payment lifecycle.
 */
public interface UserOrderService {

    /**
     * Creates a new order before initiating Razorpay payment.
     */
    UserOrders createOrder(PaymentOrderRequest request) throws Exception;

    /**
     * Updates order status based on Razorpay callback response.
     * This method handles both success and failure cases.
     */
    UserOrders updateOrder(Map<String, String> responsePayLoad);

}
