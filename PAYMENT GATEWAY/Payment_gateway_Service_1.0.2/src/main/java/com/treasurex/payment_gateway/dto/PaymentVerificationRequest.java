package com.treasurex.payment_gateway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerificationRequest {

	@NotNull(message = "orderId is required")
	private String orderId;

	@NotNull(message = "paymentId is required")
	private String paymentId;

	@NotNull(message = "signature is required")
	private String signature;
}