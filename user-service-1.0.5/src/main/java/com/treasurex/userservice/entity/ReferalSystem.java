package com.treasurex.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

	// User's phone number (10 digits)
	private String phoneNumber;

	// Who owns this referral code (the main user)
	@Column(unique = true, nullable = false)
	private String referralCode;

	// The code of the user who referred this person
	private String referredBy;

	// How many people this user has referred
	private int referralCount;

	// referal bonus
	private int referralBonus;

	// Reference back to User
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
	private User user;
}
//END