package com.treasurex.payment_gateway.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a Razorpay order with validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

	@NotNull(message = "Amount is required")
	@Min(value = 1, message = "Amount must be at least 1")
	private Integer amount; // Amount in Rupees

	@NotNull(message = "Currency is required")
	private String currency; // e.g., "INR"

	@NotNull(message = "User ID is required")
	private Long userId; // ID of the user making the payment
}
