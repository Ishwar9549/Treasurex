package com.treasurex.login_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "referral_system")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferalSystem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	// Who owns this referral code (the main user)
	private String referralCode;

	// The code of the user who referred this person
	private String referredBy;

	// How many people this user has referred
	private int referralCount;

	private int referralBonus;
}
//END