package com.treasurex.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.userservice.entity.ReferalSystem;

public interface ReferralSystemRepository extends JpaRepository<ReferalSystem, Long> {

	/**
	 * Find referral record by phone number
	 */
	Optional<ReferalSystem> findByPhoneNumber(String phoneNumber);

	/**
	 * Find referral record by referral code
	 */
	Optional<ReferalSystem> findByReferralCode(String referralCode);

}
//END 