package com.treasurex.login_service.service;

import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.ForgotPasswordRequest;
import com.treasurex.login_service.dto.LoginRequest;
import com.treasurex.login_service.dto.ReSendOtpRequest;
import com.treasurex.login_service.dto.RegisterAddressRequest;
import com.treasurex.login_service.dto.RegisterPersonalRequest;
import com.treasurex.login_service.dto.RegisterSecurityQuestionsRequest;
import com.treasurex.login_service.dto.RegisterStartRequest;
import com.treasurex.login_service.dto.RememberUserIdRequest;
import com.treasurex.login_service.dto.ResetPasswordRequest;
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
	String registerStart(RegisterStartRequest registerStartRequest);

	/**
	 * registration with personal information.
	 */
	String registerPersonal(RegisterPersonalRequest registerPersonalRequest);

	/**
	 * registration with address information.
	 */
	String registerAddress(RegisterAddressRequest registerAddressRequest);

	/**
	 * registration with security questions and answers.
	 */
	String registerSecurityQuestions(RegisterSecurityQuestionsRequest registerSecurityQuestionsRequest);

	/**
	 * Verifies a newly registered user using OTP.
	 */
	String verifyUserByOtp(UserVerifyRequest userVerifyRequest);

	/**
	 * Logs in a user with email/phone/user id after validating credentials and returns a JWT token.
	 */
	String login(LoginRequest loginRequest);

	/**
	 * Retrieves a random security question for password recovery.
	 */
	String getSecurityQuestion(ForgotPasswordRequest forgotPasswordRequest);

	/**
	 * Verifies the user’s security answer and sends an OTP if correct.
	 */
	String verifySecurityQuestionAndSendOtp(VerifySecurityAnswerRequest verifySecurityAnswerRequest);

	/**
	 * Resets the user’s password using OTP validation.
	 */
	String resetPasswordByOtp(ResetPasswordRequest resetPasswordRequest);

	/**
	 * Resends an OTP for account verification or password reset.
	 */
	String otpResend(ReSendOtpRequest reSendOtpRequest);

	/**
	 * Retrieves the user ID associated with an email/phone.
	 */
	String rememberUserId(RememberUserIdRequest rememberUserIdRequest);

	/**
	 * Changes password if the old password is correct (no OTP required).
	 */
	String changePassword(ChangePasswordRequest changePasswordRequest);
	
	/**
	 * Checking user Id is available or not.
	 */
	boolean isUserIdAvailable(String userId);
}
