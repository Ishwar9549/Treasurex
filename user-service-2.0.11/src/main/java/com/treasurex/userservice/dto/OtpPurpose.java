package com.treasurex.userservice.dto;

import com.treasurex.userservice.exception.InvalidOtpPurposeException;
import com.treasurex.userservice.security.JwtPurpose;

/**
 * Defines supported OTP purposes used across registration, authentication, and
 * recovery flows.
 */
public enum OtpPurpose {

	REGISTER_PHONE, REGISTER_EMAIL,

	FORGOT_PASSWORD_EMAIL, FORGOT_PASSWORD_PHONE,

	FORGOT_MPIN_EMAIL, FORGOT_MPIN_PHONE;

	public JwtPurpose toNextPhoneJwtPurpose() {

		switch (this) {

		case REGISTER_PHONE:
			return JwtPurpose.CREATE_PASSWORD;

		case FORGOT_PASSWORD_PHONE:
			return JwtPurpose.RESET_PASSWORD;

		case FORGOT_MPIN_PHONE:
			return JwtPurpose.RESET_MPIN;

		case REGISTER_EMAIL:
			return JwtPurpose.SET_DETAILS;

		case FORGOT_PASSWORD_EMAIL:
			return JwtPurpose.FORGOT_PASSWORD_VERIFY_EMAIL;

		default:
			throw new InvalidOtpPurposeException("Unexpected OTP purpose for phone verification: " + this);
		}
	}
}
//END