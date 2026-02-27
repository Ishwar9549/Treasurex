package com.treasurex.userservice.dto;

/**
 * Defines supported OTP purposes used across registration, authentication, and
 * recovery flows.
 */
public enum OtpPurpose {

	REGISTER_PHONE, REGISTER_EMAIL,

	FORGOT_PASSWORD, FORGOT_MPIN,

	FORGOT_PASSWORD_EMAIL, FORGOT_PASSWORD_PHONE,

	FORGOT_MPIN_EMAIL, FORGOT_MPIN_PHONE
}
//END