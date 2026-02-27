package com.treasurex.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.userservice.model.ReferralSystem;

/**
 * MyBatis mapper interface for ReferralSystem entity. Provides methods to
 * query, insert, and update referral data.
 */
@Mapper
public interface ReferralSystemMapper {

	ReferralSystem findByPhoneNumber(@Param("phoneNumber") String phoneNumber); // find by phone number

	ReferralSystem findByReferralCode(@Param("referralCode") String referralCode); // find by referral code

	int save(ReferralSystem referralSystem); // insert, returns rows affected

	int update(ReferralSystem referralSystem); // update, returns rows affected

	int updateReferralStats(@Param("id") Long id); // increment referral count and bonus
}
//END