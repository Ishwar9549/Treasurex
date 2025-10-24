package com.treasurex.user_service.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.treasurex.user_service.entity.Address;
import com.treasurex.user_service.entity.SecurityQuestion;
import com.treasurex.user_service.entity.User;
import com.treasurex.user_service.helper.Helper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final Helper helper;

	/*
	 * Mapping Register start Request to User Entity
	 */
	public User registerStartRequestToEntity(RegisterStartRequest registerStartRequest) {
		User user = User.builder().typeOfUser(registerStartRequest.getTypeOfUser())
				.userId(registerStartRequest.getUserId()).email(registerStartRequest.getEmail())
				.phoneNumber(registerStartRequest.getPhoneNumber()).password(registerStartRequest.getPassword())
				.build();
		return user;
	}

	/*
	 * Mapping Register Personal Request to User Entity
	 */
	public User registerPersonalRequestToEntity(RegisterPersonalRequest registerPersonalRequest, User user) {
		user.setFirstName(registerPersonalRequest.getFirstName());
		user.setMiddleName(registerPersonalRequest.getMiddleName());
		user.setLastName(registerPersonalRequest.getLastName());
		user.setAlternativeEmail(registerPersonalRequest.getAlternativeEmail());
		user.setAlternativePhoneNumber(registerPersonalRequest.getAlternativePhoneNumber());
		user.setDob(registerPersonalRequest.getDob());
		user.setGovtIdType(registerPersonalRequest.getGovtIdType());
		user.setGovtIdNumber(registerPersonalRequest.getGovtIdNumber());
		return user;
	}

	/*
	 * Mapping Register Address Request to User Entity
	 */
	public User registerAddressRequestToEntity(RegisterAddressRequest registerAddressRequest, User user) {
		Address address = Address.builder().addressLine1(registerAddressRequest.getAddressLine1())
				.addressLine2(registerAddressRequest.getAddressLine2()).city(registerAddressRequest.getCity())
				.state(registerAddressRequest.getState()).district(registerAddressRequest.getDistrict())
				.postalCode(registerAddressRequest.getPostalCode()).addressType(registerAddressRequest.getAddressType())
				.user(user).build();
		user.setAddress(address);
		return user;
	}

	/*
	 * Mapping Register Security Questions Request to User Entity
	 */
	public User registerSecurityQuestionsRequestToEntity(
			RegisterSecurityQuestionsRequest registerSecurityQuestionsRequest, User user) {
		List<SecurityQuestion> questions = registerSecurityQuestionsRequest.getSecurityQuestions().stream()
				.map(dto -> SecurityQuestion.builder().question(dto.getQuestion())
						.answerHash(helper.hashAnswer(dto.getAnswer())).user(user).build())
				.collect(Collectors.toList());
		user.setSecurityQuestions(questions);
		return user;
	}
}
//END