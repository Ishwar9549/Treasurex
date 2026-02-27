package com.treasurex.payment_gateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Subscription type is required")
	private String subscriptionType;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be greater than zero")
	private Integer amount;

}
//END