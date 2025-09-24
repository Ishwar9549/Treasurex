package com.treasurex.login_service.service;

import org.springframework.stereotype.Service;

import com.treasurex.login_service.dto.ApprovelRequest;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.exception.ResourceNotFoundException;
import com.treasurex.login_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;

	public String approve(ApprovelRequest approvelRequest) {
		
		User user = userRepository.findByUserId(approvelRequest.getUserId()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with user Id: " + approvelRequest.getUserId()));
		
		if (!user.isVerified()) {
			throw new RuntimeException(
					"User is not verified. Please verify your account before logging in from email.");
		}
		
		if (user.getRole().equals("NORMAL_USER")) {
			throw new RuntimeException("User "+approvelRequest.getUserId()+" is Normal User Approvel reject is not allowed");
		}
		user.setApprovalStatus(approvelRequest.getAssignStatus());
		userRepository.save(user);

		return "User id - " + user.getUserId() + " is " + user.getApprovalStatus();
	}

}
