package com.treasurex.login_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.treasurex.login_service.entity.ReferalSystem;

public interface ReferealSystemRepository extends JpaRepository<ReferalSystem, Long> {

	Optional<ReferalSystem> findByEmail(String email);

	Optional<ReferalSystem> findByReferralCode(String referralCode);
}
//END