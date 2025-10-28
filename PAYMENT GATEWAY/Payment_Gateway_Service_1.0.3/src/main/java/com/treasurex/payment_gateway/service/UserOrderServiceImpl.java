package com.treasurex.payment_gateway.service;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.treasurex.payment_gateway.entity.UserOrders;
import com.treasurex.payment_gateway.repository.UserOrdersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserOrderServiceImpl implements UserOrderService {

	private final UserOrdersRepository userOrdersRepository;

	@Value("${razorpay.keyId}")
	private String keyId;

	@Value("${razorpay.keySecret}")
	private String keySecret;

	private RazorpayClient client;

	@Override
	public UserOrders createOrder(UserOrders userOrders) throws Exception {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("amount", userOrders.getAmount() * 100); // Paisa
		jsonObject.put("currency", "INR");
		jsonObject.put("receipt", userOrders.getEmail());

		this.client = new RazorpayClient(keyId, keySecret);

		// create order in razor pay
		Order razorPayOrder = client.orders.create(jsonObject);

		userOrders.setRazorpayOrderId(razorPayOrder.get("id"));
		userOrders.setOrderStatus(razorPayOrder.get("status"));

		userOrdersRepository.save(userOrders);

		return userOrders;
	}

	@Override
	public UserOrders updateOrder(Map<String, String> responsePayLoad) {

		String razorpayOrderId = responsePayLoad.get("razorpay_order_id");

		UserOrders userOrder = userOrdersRepository.findByRazorpayOrderId(razorpayOrderId);

		userOrder.setOrderStatus("PAYMENT_COMPLETED");

		UserOrders updatedOrder = userOrdersRepository.save(userOrder);

		return updatedOrder;
	}

}
