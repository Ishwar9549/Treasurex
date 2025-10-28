package com.treasurex.payment_gateway.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	private String refundId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private String status; // CREATED, PAID, FAILED, REFUNDED

	private String currency;

	@Column(length = 500)
	private String notes; // optional extra info

	private final Instant createdAt = Instant.now();
}
