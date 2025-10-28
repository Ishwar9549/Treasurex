package com.treasurex.payment_gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.payment_gateway.entity.UserOrders;

public interface UserOrdersRepository extends JpaRepository<UserOrders, Long> {

	public UserOrders findByRazorpayOrderId(String orderId);

}
