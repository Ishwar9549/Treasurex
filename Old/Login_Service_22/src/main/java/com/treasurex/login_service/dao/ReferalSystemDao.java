package com.treasurex.login_service.dao;

import java.util.List;
import java.util.Optional;

import com.treasurex.login_service.entity.ReferalSystem;

public interface ReferalSystemDao {

	void save(ReferalSystem referalSystem);

	void update(ReferalSystem referalSystem);

	Optional<ReferalSystem> findByEmail(String email);

	Optional<ReferalSystem> findByReferralCode(String referralCode);

	List<ReferalSystem> findAll();

	void delete(ReferalSystem referalSystem);
}
