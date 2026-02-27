package com.treasurex.payment_gateway.service;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.treasurex.payment_gateway.Mapper.UserOrdersMapper;
import com.treasurex.payment_gateway.dto.PaymentOrderRequest;
import com.treasurex.payment_gateway.model.UserOrders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOrderServiceImpl implements UserOrderService {

	// MyBatis mapper for DB operations
	private final UserOrdersMapper userOrdersMapper;

	// Razorpay credentials (from application.properties)
	@Value("${razorpay.keyId}")
	private String keyId;

	@Value("${razorpay.keySecret}")
	private String keySecret;

	/**
	 * Creates a Razorpay order and stores initial order details in DB
	 */
	@Override
	public UserOrders createOrder(PaymentOrderRequest request) throws Exception {

		log.info("Creating order for email: {}", request.getEmail());

		UserOrders userOrders = new UserOrders();

		// Initialize Razorpay client
		RazorpayClient client = new RazorpayClient(keyId, keySecret);

		// Prepare Razorpay order payload
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("amount", request.getAmount() * 100); // convert to paise
		jsonObject.put("currency", "INR");
		jsonObject.put("receipt", request.getEmail());

		// Create order in Razorpay
		Order razorPayOrder = client.orders.create(jsonObject);

		log.info("Razorpay order created with ID: {}");

		// Populate order entity
		userOrders.setEmail(request.getEmail());
		userOrders.setSubscriptionType(request.getSubscriptionType());
		userOrders.setAmount(request.getAmount());
		userOrders.setOrderStatus("CREATED");
		userOrders.setRazorpayOrderId(razorPayOrder.get("id"));

		// Save order to DB
		userOrdersMapper.save(userOrders);

		log.info("Order saved in DB with status CREATED");

		return userOrders;
	}

	/**
	 * Updates order status based on Razorpay callback response
	 */
	@Override
	public UserOrders updateOrder(Map<String, String> responsePayLoad) {

		log.info("Received Razorpay callback payload");

		// Print all received callback parameters
		responsePayLoad.forEach((k, v) -> log.info("{} : {}", k, v));

		String razorpayOrderId = responsePayLoad.get("razorpay_order_id");

		// Fetch order from DB
		UserOrders userOrder = userOrdersMapper.findByRazorpayOrderId(razorpayOrderId);

		if (userOrder == null) {
			log.warn("No order found for Razorpay Order ID: {}", razorpayOrderId);
			return null;
		}

		// SUCCESS case
		if (responsePayLoad.containsKey("razorpay_payment_id")) {
			userOrder.setOrderStatus("SUCCESS");
			userOrdersMapper.update(userOrder);
			log.info("Order marked as SUCCESS for orderId: {}", razorpayOrderId);
		}
		// FAILURE / CANCELLED case
		else {
			userOrder.setOrderStatus("FAILED");
			userOrdersMapper.update(userOrder);
			log.info("Order marked as FAILED for orderId: {}", razorpayOrderId);
		}
		return userOrder;
	}
}
//END
