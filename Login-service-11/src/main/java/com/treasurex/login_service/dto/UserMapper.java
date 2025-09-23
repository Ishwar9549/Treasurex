package com.treasurex.login_service.dto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Component;

import com.treasurex.login_service.entity.Address;
import com.treasurex.login_service.entity.SecurityQuestion;
import com.treasurex.login_service.entity.User;

@Component
public class UserMapper {

	public User dtoToEntity(RegisterRequest request) {
		if (request == null)
			return null;

		User user = User.builder().userId(request.getUserId()).role(request.getRole()).firstName(request.getFirstName())
				.middleName(request.getMiddleName()).lastName(request.getLastName()).email(request.getEmail())
				.password(request.getPassword()).phoneNumber(request.getPhoneNumber())
				.alternativePhoneNumber(request.getAlternativePhoneNumber())
				.alternativeEmail(request.getAlternativeEmail()).dob(request.getDob()).idType(request.getIdType())
				.idNumber(request.getIdNumber()).build();

		if (request.getAddress() != null) {
			Address address = Address.builder().addressLine1(request.getAddress().getAddressLine1())
					.addressLine2(request.getAddress().getAddressLine2()).city(request.getAddress().getCity())
					.stateProvince(request.getAddress().getState()).district(request.getAddress().getDistrict())
					.postalCode(request.getAddress().getPostalCode()).type(request.getAddress().getType()).user(user)
					.build();
			user.setAddress(address);
		}

		if (request.getSecurityQuestions() != null) {
			List<SecurityQuestion> questions = request.getSecurityQuestions().stream()
					.map(q -> SecurityQuestion.builder().question(q.getQuestion()).answerHash(hashAnswer(q.getAnswer()))
							.user(user)
							.build())
					.toList();
			user.setSecurityQuestions(questions);
		}
		return user;
	}

	private String hashAnswer(String answer) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(answer.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			throw new RuntimeException("Error hashing answer", e);
		}
	}
}