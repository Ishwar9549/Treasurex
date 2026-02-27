package com.treasurex.userservice.helper;

import org.springframework.stereotype.Component;

import com.treasurex.userservice.dto.AdvisorDetailsRequest;
import com.treasurex.userservice.dto.BusinessDetailsRequest;
import com.treasurex.userservice.dto.RegisterPhoneNumberRequest;
import com.treasurex.userservice.dto.UserDetailsRequest;
import com.treasurex.userservice.model.AdvisorDetails;
import com.treasurex.userservice.model.BusinessDetails;
import com.treasurex.userservice.model.User;
import com.treasurex.userservice.model.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Converter class to map request DTOs to entity objects. Handles mapping for
 * User, UserDetails, AdvisorDetails, and BusinessDetails. Logging is at DEBUG
 * level for development purposes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserConverter {

	/**
	 * Mapping RegisterPhoneNumberRequest → User entity.
	 */
	public User registerPhoneNumberRequestToEntity(RegisterPhoneNumberRequest request) {
		User user = User.builder().userType(request.getTypeOfUser()).phoneNumber(request.getPhoneNumber()).build();
		log.debug("Mapped RegisterPhoneNumberRequest to User: phoneNumber={}, type={}", user.getPhoneNumber(),
				user.getUserType());
		return user;
	}

	/**
	 * Mapping UserDetailsRequest → UserDetails entity.
	 */
	public UserDetails setUserDetailsRequestToEntity(User user, UserDetailsRequest request) {
		UserDetails userDetails = UserDetails.builder().firstName(request.getFirstName())
				.lastName(request.getLastName()).userId(user.getId()).build();
		log.debug("Set UserDetails for UserId: {}", user.getId());
		return userDetails;
	}

	/**
	 * Mapping AdvisorDetailsRequest → AdvisorDetails entity.
	 */
	public AdvisorDetails setAdvisorDetailsRequestToEntity(User user, AdvisorDetailsRequest request) {
		AdvisorDetails advisorDetails = AdvisorDetails.builder().firstName(request.getFirstName())
				.lastName(request.getLastName()).arnNumber(request.getArnNumber()).nomineeName(request.getNomineeName())
				.nomineeContactNumber(request.getNomineeContactNumber()).userId(user.getId()).build();
		log.debug("Set AdvisorDetails for UserId: {}", user.getId());
		return advisorDetails;
	}

	/**
	 * Mapping BusinessDetailsRequest → BusinessDetails entity.
	 */
	public BusinessDetails setBusinessDetailsRequestToEntity(User user, BusinessDetailsRequest request) {
		BusinessDetails businessDetails = BusinessDetails.builder().fullName(request.getFullName())
				.businessName(request.getBusinessName()).businessPhone(request.getBusinessPhone())
				.businessPlace(request.getBusinessPlace()).panNumber(request.getPanNumber())
				.gstNumber(request.getGstNumber()).nomineeName(request.getNomineeName())
				.nomineeContactNumber(request.getNomineeContactNumber()).bankName(request.getBankName())
				.accountNumber(request.getAccountNumber()).ifscCode(request.getIfscCode())
				.businessWebSite(request.getBusinessWebSite()).businessType(request.getBusinessType())
				.bio(request.getBio()).userId(user.getId()).build();

		log.debug("Set BusinessDetails for UserId: {}", user.getId());
		return businessDetails;
	}
}
//END