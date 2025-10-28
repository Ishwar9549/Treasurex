package com.treasurex.payment_gateway.service;

import java.util.Map;

import com.treasurex.payment_gateway.entity.UserOrders;

public interface UserOrderService {

	public UserOrders createOrder(UserOrders userOrders) throws Exception;

	public UserOrders updateOrder(Map<String, String> responsePayLoad);

}
