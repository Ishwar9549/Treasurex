package com.treasurex.payment_gateway.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.payment_gateway.entity.PaymentTransaction;

/**
 * Repository for PaymentTransaction Entity
 */
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

	List<PaymentTransaction> findByUserId(Long userId);

	Optional<PaymentTransaction> findByOrderId(String orderId);
}
