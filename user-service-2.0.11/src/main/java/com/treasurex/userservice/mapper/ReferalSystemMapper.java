package com.treasurex.userservice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.treasurex.userservice.model.ReferalSystem;

/**
 * MyBatis mapper interface for ReferalSystem entity. Provides methods to query,
 * insert, and update referral data.
 */
@Mapper
public interface ReferalSystemMapper {

	ReferalSystem findByPhoneNumber(@Param("phoneNumber") String phoneNumber); // find by phone number

	ReferalSystem findByReferralCode(@Param("referralCode") String referralCode); // find by referral code 

	int save(ReferalSystem referalSystem); // insert, returns rows affected

	int update(ReferalSystem referalSystem); // update, returns rows affected

	int updateReferralStats(@Param("id") Long id); // update referral statistics
}
//END