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

public interface UserService {

	/*
	 * User registration with phone. Sends OTP through SMS to Phone number.
	 */
	ApiResponse<Map<String, String>> registerPhoneNumber(RegisterPhoneNumberRequest request);

	/*
	 * Verify phone Number. through OTP.
	 */
	ApiResponse<Map<String, String>> verifyPhoneNumber(String authorizationHeader, OtpVerifyRequest request);

	/*
	 * Create and set password for verified user
	 */
	ApiResponse<Map<String, String>> createPassword(String authorizationHeader, PasswordRequest request);

	/**
	 * Checks whether a user name is already taken or available for registration
	 */
	ApiResponse<Void> checkUsernameAvailability(UserNameCheckRequest request);

	/*
	 * set email and send OTP to email
	 */
	ApiResponse<Map<String, String>> registerEmail(String authorizationHeader, RegisterEmailRequest request);

	/*
	 * verify email through OTP recived from mail
	 */
	ApiResponse<Map<String, String>> verifyEmail(String authorizationHeader, OtpVerifyRequest request);

	/*
	 * Set details for NORMAL_USER
	 */
	ApiResponse<Map<String, String>> setUserDetails(String authorizationHeader, UserDetailsRequest request);

	/*
	 * Set details for ADVIOSOR_USER
	 */
	ApiResponse<Map<String, String>> setAdvisorDetails(String authorizationHeader, AdvisorDetailsRequest request);

	/*
	 * Set details for BUSINESS_USER
	 */
	ApiResponse<Map<String, String>> setBusinessDetails(String authorizationHeader, BusinessDetailsRequest request);

	/*
	 * Set MPIN
	 */
	ApiResponse<Map<String, String>> setMpin(String authorizationHeader, MpinRequest request);

	/*
	 * Login with Email / Phone number and password(returns JWT token if valid)
	 */
	ApiResponse<Map<String, String>> login(LoginRequest request);

	/**
	 * verify MPIN.
	 */
	ApiResponse<Map<String, String>> verifyMpin(String authorizationHeader, String mpin);

	/*
	 * get OTP to Email
	 */
	ApiResponse<Map<String, String>> getEmailOtp(EmailRequest request);

	/*
	 * get OTP to Phone number for forgot password
	 */
	ApiResponse<Map<String, String>> getPhoneNumberOtp(String authorizationHeader, PhoneNumberRequest request);

	/*
	 * Reset Password
	 */
	ApiResponse<Map<String, String>> resetPassword(String authorizationHeader, PasswordRequest request);

	/*
	 * Reset MPIN
	 */
	ApiResponse<Map<String, String>> resetMpin(String authorizationHeader, MpinRequest request);

	/*
	 * Re-send OTP
	 */
	ApiResponse<Map<String, String>> reSendOtp(ReSendOtpRequest request);

	/*
	 * change password if old password is known
	 */
	ApiResponse<Void> changePassword(String authorizationHeader, ChangePasswordRequest request);

	/**
	 * Change MPIN. if old MPIN is known
	 */
	ApiResponse<Void> changeMpin(String authorizationHeader, ChangeMpinRequest request);

	/*
	 * Remember User Id
	 */
	ApiResponse<Void> rememberUserId(RememberUserNameRequest request);

}
//END 
