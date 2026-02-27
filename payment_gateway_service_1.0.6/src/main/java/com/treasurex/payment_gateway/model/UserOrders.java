package com.treasurex.payment_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing user_orders table.
 * Stores subscription and payment related details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrders {

    /**
     * Primary key (auto-generated)
     */
    private Long id;

    /**
     * User email address
     */
    private String email;

    /**
     * Subscription type
     * Example: BASIC, SILVER, GOLD, PLATINUM, PRIME
     */
    private String subscriptionType;

    /**
     * Subscription amount (in INR)
     */
    private Integer amount;

    /**
     * Order status
     * Example: CREATED, PAID, FAILED
     */
    private String orderStatus;

    /**
     * Razorpay generated order ID
     */
    private String razorpayOrderId;

}
