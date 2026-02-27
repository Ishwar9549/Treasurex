package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ReferalSystem entity representing referral relationships between users.
 * Tracks referral codes, counts, and bonuses. Suitable for use with MyBatis.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferalSystem {

	private Long id; // Unique DB id

	private String phoneNumber; // Reference phone number

	private String referralCode; // Referral code owned by this user

	private String referredBy; // Referral code of the user who referred this person

	private int referralCount; // Number of users this user has referred

	private int referralBonus; // Referral bonus (points, credits, or amount)
}
//END