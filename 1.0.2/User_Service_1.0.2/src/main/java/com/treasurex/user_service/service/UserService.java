package com.treasurex.user_service.service;

import java.util.Map;

import com.treasurex.user_service.dto.ApiResponse;
import com.treasurex.user_service.dto.ChangePasswordRequest;
import com.treasurex.user_service.dto.ForgotPasswordRequest;
import com.treasurex.user_service.dto.LoginRequest;
import com.treasurex.user_service.dto.ReSendOtpRequest;
import com.treasurex.user_service.dto.RegisterAddressRequest;
import com.treasurex.user_service.dto.RegisterPersonalRequest;
import com.treasurex.user_service.dto.RegisterSecurityQuestionsRequest;
import com.treasurex.user_service.dto.RegisterStartRequest;
import com.treasurex.user_service.dto.RememberUserIdRequest;
import com.treasurex.user_service.dto.ResetPasswordRequest;
import com.treasurex.user_service.dto.UserIdCheckRequest;
import com.treasurex.user_service.dto.UserVerifyRequest;
import com.treasurex.user_service.dto.VerifySecurityAnswerRequest;

/**
 * Service interface defining user-related operations like registration, login,
 * authentication, password reset, and OTP verification.
 */
public interface UserService {

	/**
	 * Starts user registration with basic details.
	 */
	ApiResponse<Map<String, String>> registerStart(RegisterStartRequest request);

	/**
	 * registration with personal information. that accept the Authorization header
	 * token to proceed with registration steps
	 */
	ApiResponse<Map<String, String>> registerPersonalWithToken(String authorizationHeader,
			RegisterPersonalRequest request);

	/**
	 * registration with address information. that accept the Authorization header
	 * token to proceed with registration steps
	 */
	ApiResponse<Map<String, String>> registerAddressWithToken(String authorizationHeader,
			RegisterAddressRequest request);

	/**
	 * registration with security questions and answers.that accept the
	 * Authorization header token to proceed with registration steps
	 */
	ApiResponse<Void> registerSecurityQuestionsWithToken(String authorizationHeader,
			RegisterSecurityQuestionsRequest request);

	/**
	 * Verifies a newly registered user using OTP.now returns
	 * RegistrationVerifyResponse (so it can include token)
	 */
	ApiResponse<Map<String, String>> verifyUserByOtp(UserVerifyRequest request);

	/**
	 * Logs in a user with email/phone/user id after validating credentials and
	 * returns a JWT token.
	 */
	ApiResponse<Map<String, String>> login(LoginRequest request);

	/**
	 * Retrieves a random security question for password recovery.
	 */
	ApiResponse<Map<String, String>> getSecurityQuestion(ForgotPasswordRequest request);

	/**
	 * Verifies the user’s security answer and sends an OTP if correct.
	 */
	ApiResponse<Map<String, String>> verifySecurityQuestionAndSendOtp(VerifySecurityAnswerRequest request);

	/**
	 * Resets the user’s password using OTP validation.
	 */
	ApiResponse<Void> resetPasswordByOtp(ResetPasswordRequest request);

	/**
	 * Resends an OTP for account verification or password reset.
	 */
	ApiResponse<Void> otpResend(ReSendOtpRequest request);

	/**
	 * Retrieves the user ID associated with an email/phone.
	 */
	ApiResponse<Void> rememberUserId(RememberUserIdRequest request);

	/**
	 * Changes password if the old password is correct (no OTP required).
	 */
	ApiResponse<Void> changePassword(ChangePasswordRequest request);

	/**
	 * Checking user Id is available or not.
	 */
	ApiResponse<Void> isUserIdAvailable(UserIdCheckRequest request);
}
//END 