package com.treasurex.login_service.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.treasurex.login_service.entity.Address;
import com.treasurex.login_service.entity.AdvisorUser;
import com.treasurex.login_service.entity.BusinessUser;
import com.treasurex.login_service.entity.SecurityQuestion;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.helper.Helper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final Helper helper;

	/*
	 * Mapping Register start Request to User Entity
	 */
	public User registerStartRequestToEntity(RegisterStartRequest request) {
		User user = User.builder().typeOfUser(request.getTypeOfUser()).userId(request.getUserId())
				.email(request.getEmail()).phoneNumber(request.getPhoneNumber()).password(request.getPassword())
				.build();
		return user;
	}

	/*
	 * Mapping Register Personal Request to User Entity
	 */
	public User registerPersonalRequestToEntity(RegisterPersonalRequest request, User user) {
		user.setFirstName(request.getFirstName());
		user.setMiddleName(request.getMiddleName());
		user.setLastName(request.getLastName());
		user.setAlternativeEmail(request.getAlternativeEmail());
		user.setAlternativePhoneNumber(request.getAlternativePhoneNumber());
		user.setDob(request.getDob());
		user.setGovtIdType(request.getGovtIdType());
		user.setGovtIdNumber(request.getGovtIdNumber());
		return user;
	}

	/*
	 * Mapping Register Address Request to User Entity
	 */
	public User registerAddressRequestToEntity(RegisterAddressRequest request, User user) {
		Address address = Address.builder().addressLine1(request.getAddressLine1())
				.addressLine2(request.getAddressLine2()).city(request.getCity()).state(request.getState())
				.district(request.getDistrict()).postalCode(request.getPostalCode())
				.addressType(request.getAddressType()).user(user).build();
		user.setAddress(address);
		return user;
	}

	/*
	 * Mapping Register Security Questions Request to User Entity
	 */
	public User registerSecurityQuestionsRequestToEntity(RegisterSecurityQuestionsRequest request, User user) {
		List<SecurityQuestion> questions = request.getSecurityQuestions().stream()
				.map(dto -> SecurityQuestion.builder().question(dto.getQuestion())
						.answerHash(helper.hashAnswer(dto.getAnswer())).user(user).build())
				.collect(Collectors.toList());
		user.setSecurityQuestions(questions);
		return user;
	}

	/*
	 * Mapping Business Details Request Request to User Entity
	 */
	public User BusinessDetailsRequestToEntity(BusinessDetailsRequest request, User user) {
		BusinessUser businessUser = BusinessUser.builder().businessName(request.getBusinessName())
				.businessPhone(request.getBusinessPhone()).businessPlace(request.getBusinessPlace())
				.panNumber(request.getPanNumber()).gstNumber(request.getGstNumber())
				.nomineeName(request.getNomineeName()).nomineeContactNumber(request.getNomineeContactNumber())
				.bankName(request.getBankName()).accountNumber(request.getAccountNumber())
				.ifscCode(request.getIfscCode()).bio(request.getBio()).user(user).build();

		user.setBusinessUser(businessUser);
		return user;
	}

	/*
	 * Mapping Advisor Details Request Request to User Entity
	 */
	public User AdvisorDetailsRequestToEntity(AdvisorDetailsRequest request, User user) {

		AdvisorUser advisorUser = AdvisorUser.builder().arnNumber(request.getArnNumber())
				.nomineeName(request.getNomineeName()).nomineeContactNumber(request.getNomineeContactNumber())
				.certificationId(request.getCertificationId()).experienceYears(request.getExperienceYears()).user(user)
				.build();
		user.setAdvisorUser(advisorUser);
		return user;
	}

}
//END