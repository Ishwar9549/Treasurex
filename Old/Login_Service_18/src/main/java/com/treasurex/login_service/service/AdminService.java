package com.treasurex.login_service.service;

import org.springframework.stereotype.Service;

import com.treasurex.login_service.dto.ApiResponse;
import com.treasurex.login_service.dto.ApprovalRequest;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.exception.ResourceNotFoundException;
import com.treasurex.login_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;

	/**
	 * End point to approve requests Business & Advisor accounts require ADMIN
	 * approval ADMIN can approve/reject users.
	 */
	public ApiResponse<Void> approve(ApprovalRequest request) {
		User user = findUserByLoginId(request.getLoginId());

		if (!user.isVerified()) {
			throw new RuntimeException(
					"User is not verified. Please verify your account before logging in from email.");
		}

		if (user.getTypeOfUser().equals("NORMAL_USER")) {
			throw new RuntimeException(
					"User " + request.getLoginId() + " is Normal User Approvel / Reject is not allowed");
		}
		user.setApprovalStatus(request.getAssignStatus());
		userRepository.save(user);

		String message = "User id - " + user.getUserId() + " is " + user.getApprovalStatus();

		return ApiResponse.success(null, message);
	}

	/**
	 * Find user by email, phone, or userId for flexibility.
	 */
	private User findUserByLoginId(String loginId) {
		if (loginId.contains("@")) {
			return userRepository.findByEmail(loginId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with Email: " + loginId));
		} else if (loginId.matches("\\d{10}")) {
			return userRepository.findByPhoneNumber(loginId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with Phone: " + loginId));
		} else {
			return userRepository.findByUserId(loginId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with User ID: " + loginId));
		}
	}
}
//END
