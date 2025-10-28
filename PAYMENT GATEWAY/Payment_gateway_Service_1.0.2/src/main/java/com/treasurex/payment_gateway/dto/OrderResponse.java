package com.treasurex.payment_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
	private String orderId;
	private int amount; // in paise
	private String currency;
	private String status; // CREATED, PAID, etc.
}
