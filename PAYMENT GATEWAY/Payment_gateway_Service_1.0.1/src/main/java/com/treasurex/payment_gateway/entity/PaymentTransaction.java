package com.treasurex.payment_gateway.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a Payment Transaction stored in database.
 */
@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String orderId;

	private String paymentId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private String status; // CREATED, PAID, FAILED
}
