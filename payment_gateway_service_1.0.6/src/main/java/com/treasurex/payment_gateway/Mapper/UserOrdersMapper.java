package com.treasurex.payment_gateway.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.payment_gateway.model.UserOrders;

/**
 * MyBatis Mapper for user_orders table. Handles all DB operations related to
 * user payment orders.
 */
@Mapper
public interface UserOrdersMapper {

	/**
	 * Fetch order details using Razorpay Order ID
	 */
	UserOrders findByRazorpayOrderId(@Param("razorpayOrderId") String razorpayOrderId);

	/**
	 * Fetch order details using user email
	 */
	UserOrders findByEmail(@Param("email") String email);

	/**
	 * Insert a new order record into database
	 */
	int save(UserOrders userOrders);

	/**
	 * Update existing order (mostly used for payment status update)
	 */
	int update(UserOrders userOrders);
}
//END