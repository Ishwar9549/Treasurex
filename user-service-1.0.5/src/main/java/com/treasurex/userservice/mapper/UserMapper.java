package com.treasurex.userservice.mapper;

import org.springframework.stereotype.Component;

import com.treasurex.userservice.dto.AdvisorDetailsRequest;
import com.treasurex.userservice.dto.BusinessDetailsRequest;
import com.treasurex.userservice.dto.RegisterPhoneNumberRequest;
import com.treasurex.userservice.dto.UserDetailsRequest;
import com.treasurex.userservice.entity.AdvisorDetails;
import com.treasurex.userservice.entity.BusinessDetails;
import com.treasurex.userservice.entity.User;
import com.treasurex.userservice.entity.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserMapper {

	/*
	 * Mapping Register Phone Number Request to User Entity
	 */
	public User registerPhoneNumberRequestToEntity(RegisterPhoneNumberRequest request) {
		User user = User.builder().typeOfUser(request.getTypeOfUser()).phoneNumber(request.getPhoneNumber()).build();
		log.debug("Mapped RegisterPhoneNumberRequest to User: {}", user.getPhoneNumber());
		return user;
	}

	/*
	 * Mapping set User Details Request to User Entity
	 */
	public User setUserDetailsRequestToEntity(User user, UserDetailsRequest request) {

		// Create UserDetails entity and link to User
		UserDetails userDetails = UserDetails.builder().firstName(request.getFirstName())
				.lastName(request.getLastName()).user(user).build();

		// Update user fields
		user.setEmail(request.getEmail());
		user.setUserDetails(userDetails);
		user.setUserName(request.getUserName());
		user.setReferalSystem(null); // intentional reset; adjust if needed

		log.debug("Set UserDetails for User: {}", user.getUserName());
		return user;
	}

	/*
	 * Mapping set Advisor Details Request to User Entity
	 */
	public User setAdvisorDetailsRequestToEntity(User user, AdvisorDetailsRequest request) {

		AdvisorDetails advisorDetails = AdvisorDetails.builder().firstName(request.getFirstName())
				.lastName(request.getLastName()).arnNumber(request.getArnNumber()).nomineeName(request.getNomineeName())
				.nomineeContactNumber(request.getNomineeContactNumber()).user(user).build();

		user.setUserName(request.getUserName());
		user.setAdvisorDetails(advisorDetails);

		log.debug("Set AdvisorDetails for User: {}", user.getUserName());
		return user;
	}

	/*
	 * Mapping set Business Details Request to User Entity
	 */
	public User setBusinessDetailsRequestToEntity(User user, BusinessDetailsRequest request) {

		BusinessDetails businessDetails = BusinessDetails.builder().fullName(request.getFullName())
				.businessName(request.getBusinessName()).businessPhone(request.getBusinessPhone())
				.businessPlace(request.getBusinessPlace()).panNumber(request.getPanNumber())
				.gstNumber(request.getGstNumber()).nomineeName(request.getNomineeName())
				.nomineeContactNumber(request.getNomineeContactNumber()).bankName(request.getBankName())
				.accountNumber(request.getAccountNumber()).ifscCode(request.getIfscCode())
				.businessWebSite(request.getBusinessWebSite()).businessType(request.getBusinessType())
				.bio(request.getBio()).user(user).build();

		user.setUserName(request.getUserName());
		user.setBusinessDetails(businessDetails);

		log.debug("Set BusinessDetails for User: {}", user.getUserName());
		return user;

	}
}
//END