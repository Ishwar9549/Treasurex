package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents referral relationships between users. Tracks referral codes,
 * counts, and bonuses. Suitable for MyBatis or other ORM usage.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralSystem {

	private Long id; // Primary key / unique DB id

	private String phoneNumber; // Phone number of the user

	private String referralCode; // This user's own referral code

	private String referredBy; // Referral code of the user who referred this user

	private int referralCount; // Number of users this user has successfully referred

	private int referralBonus; // Bonus earned via referrals (points, credits, or amount)
}
//END