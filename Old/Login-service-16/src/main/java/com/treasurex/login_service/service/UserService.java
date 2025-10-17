package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterAddressRequest;
import com.treasurex.login_service.dto.RegisterPersonalRequest;
import com.treasurex.login_service.dto.RegisterSecurityQuestionsRequest;
import com.treasurex.login_service.dto.RegisterStartRequest;
import com.treasurex.login_service.dto.RegistrationVerifyResponse;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
import com.treasurex.login_service.dto.UserIdCheckRequest;
import com.treasurex.login_service.dto.UserVerifyRequest;
import com.treasurex.login_service.dto.VerifySecurityAnswerRequest;

/**
 * Service interface defining user-related operations like registration, login,
 * authentication, password reset, and OTP verification.
 */
public interface UserService {

	/**
	 * Starts user registration with basic details.
	 */
	String registerStart(RegisterStartRequest request);

	/**
	 * registration with personal information. that accept the Authorization header
	 * token to proceed with registration steps
	 */
	String registerPersonalWithToken(String authorizationHeader, RegisterPersonalRequest request);

	/**
	 * registration with address information. that accept the Authorization header
	 * token to proceed with registration steps
	 */
	String registerAddressWithToken(String authorizationHeader, RegisterAddressRequest request);

	/**
	 * registration with security questions and answers.that accept the
	 * Authorization header token to proceed with registration steps
	 */
	String registerSecurityQuestionsWithToken(String authorizationHeader, RegisterSecurityQuestionsRequest request);

	/**
	 * Verifies a newly registered user using OTP.now returns
	 * RegistrationVerifyResponse (so it can include token)
	 */
	RegistrationVerifyResponse verifyUserByOtp(UserVerifyRequest request);

	/**
	 * Logs in a user with email/phone/user id after validating credentials and
	 * returns a JWT token.
	 */
	String login(LoginRequest request);

	/**
	 * Retrieves a random security question for password recovery.
	 */
	String getSecurityQuestion(ForgotPasswordRequest request);

	/**
	 * Verifies the user’s security answer and sends an OTP if correct.
	 */
	String verifySecurityQuestionAndSendOtp(VerifySecurityAnswerRequest request);

	/**
	 * Resets the user’s password using OTP validation.
	 */
	String resetPasswordByOtp(ResetPasswordRequest request);

	/**
	 * Resends an OTP for account verification or password reset.
	 */
	String otpResend(ReSendOtpRequest request);

	/**
	 * Retrieves the user ID associated with an email/phone.
	 */
	String rememberUserId(RememberUserIdRequest request);

	/**
	 * Changes password if the old password is correct (no OTP required).
	 */
	String changePassword(ChangePasswordRequest request);

	/**
	 * Checking user Id is available or not.
	 */
	boolean isUserIdAvailable(UserIdCheckRequest request);

}
