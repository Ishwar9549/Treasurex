package com.treasurex.userservice.service;

import java.util.Map;

import com.treasurex.userservice.dto.AdvisorDetailsRequest;
import com.treasurex.userservice.dto.ApiResponse;
import com.treasurex.userservice.dto.BusinessDetailsRequest;
import com.treasurex.userservice.dto.ChangeMpinRequest;
import com.treasurex.userservice.dto.ChangePasswordRequest;
import com.treasurex.userservice.dto.EmailRequest;
import com.treasurex.userservice.dto.LoginRequest;
import com.treasurex.userservice.dto.MpinRequest;
import com.treasurex.userservice.dto.OtpVerifyRequest;
import com.treasurex.userservice.dto.PasswordRequest;
import com.treasurex.userservice.dto.PhoneNumberRequest;
import com.treasurex.userservice.dto.ReSendOtpRequest;
import com.treasurex.userservice.dto.RegisterEmailRequest;
import com.treasurex.userservice.dto.RegisterPhoneNumberRequest;
import com.treasurex.userservice.dto.RememberUserNameRequest;
import com.treasurex.userservice.dto.UserDetailsRequest;
import com.treasurex.userservice.dto.UserNameCheckRequest;
import com.treasurex.userservice.dto.VerifyMpinRequest;

/**
 * Service interface for user-related operations. Handles registration, login,
 * OTP verification, password/MPIN management, and user profile details.
 */
public interface UserService {

	/**
	 * Register a new user using phone number. Sends OTP to the phone.
	 */
	ApiResponse<Map<String, String>> registerPhone(RegisterPhoneNumberRequest request);

	/**
	 * Verify phone number using OTP sent during registration.
	 */
	ApiResponse<Map<String, String>> verifyRegistrationPhone(String authorizationHeader, OtpVerifyRequest request);

	/**
	 * Set password for a verified user.
	 */
	ApiResponse<Map<String, String>> createPassword(String authorizationHeader, PasswordRequest request);

	/**
	 * Check whether a username is available for registration.
	 */
	ApiResponse<Void> checkUsernameAvailability(UserNameCheckRequest request);

	/**
	 * Register email for the user and send OTP to email.
	 */
	ApiResponse<Map<String, String>> registerEmail(String authorizationHeader, RegisterEmailRequest request);

	/**
	 * Verify email using OTP sent during email registration.
	 */
	ApiResponse<Map<String, String>> verifyRegistrationEmail(String authorizationHeader, OtpVerifyRequest request);

	/**
	 * Set profile details for a NORMAL_USER.
	 */
	ApiResponse<Map<String, String>> setUserDetails(String authorizationHeader, UserDetailsRequest request);

	/**
	 * Set personal and professional details for an ADVISOR_USER.
	 */
	ApiResponse<Map<String, String>> setAdvisorDetails(String authorizationHeader, AdvisorDetailsRequest request);

	/**
	 * Set business-related details for a BUSINESS_USER.
	 */
	ApiResponse<Map<String, String>> setBusinessDetails(String authorizationHeader, BusinessDetailsRequest request);

	/**
	 * Set MPIN for the user.
	 */
	ApiResponse<Map<String, String>> createMpin(String authorizationHeader, MpinRequest request);

	/**
	 * Authenticate user via email or phone and password. Returns JWT token if
	 * successful.
	 */
	ApiResponse<Map<String, String>> login(LoginRequest request);

	/**
	 * Verify MPIN for an authenticated user.
	 */
	ApiResponse<Map<String, String>> verifyMpin(String authorizationHeader, VerifyMpinRequest request);

	/**
	 * Send OTP to email for forgot password flow.
	 */
	ApiResponse<Map<String, String>> sendForgotOtpToEmail(EmailRequest request);

	/**
	 * Verify email OTP during forgot password flow.
	 */
	ApiResponse<Map<String, String>> verifyForgotEmail(String authorizationHeader, OtpVerifyRequest request);

	/**
	 * Send OTP to phone for forgot password flow.
	 */
	ApiResponse<Map<String, String>> sendForgotOtpToPhone(String authorizationHeader, PhoneNumberRequest request);

	/**
	 * Verify phone OTP during forgot password flow.
	 */
	ApiResponse<Map<String, String>> verifyForgotPhone(String authorizationHeader, OtpVerifyRequest request);

	/**
	 * Reset password after verifying user via forgot password flow.
	 */
	ApiResponse<Map<String, String>> resetPassword(String authorizationHeader, PasswordRequest request);

	/**
	 * Reset MPIN after verifying user via forgot MPIN flow.
	 */
	ApiResponse<Map<String, String>> resetMpin(String authorizationHeader, MpinRequest request);

	/**
	 * Re-send OTP for registration or verification flows.
	 */
	ApiResponse<Map<String, String>> reSendOtp(ReSendOtpRequest request);

	/**
	 * Change password for authenticated users who know their current password.
	 */
	ApiResponse<Void> changePassword(String authorizationHeader, ChangePasswordRequest request);

	/**
	 * Change MPIN for authenticated users who know their current MPIN.
	 */
	ApiResponse<Void> changeMpin(String authorizationHeader, ChangeMpinRequest request);

	/**
	 * Remember User ID based on email or phone.
	 */
	ApiResponse<Void> rememberUserId(RememberUserNameRequest request);
}
//END