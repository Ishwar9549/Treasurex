package com.treasurex.userservice.enums;

/**
 * Defines the purpose of a JWT token in the UserService system. Used to
 * differentiate tokens for registration, password reset, MPIN, and profile
 * access.
 */
public enum JwtPurpose {

	// Registration related

	/** JWT issued during email registration initiation. */
	REGISTER_EMAIL,

	/** JWT used to verify phone number during registration. */
	REGISTER_VERIFY_PHONE,

	/** JWT used to verify email during registration. */
	REGISTER_VERIFY_EMAIL,

	// Password / MPIN recovery

	/** JWT used to verify phone number before password reset. */
	FORGOT_PASSWORD_VERIFY_PHONE,

	/** JWT used to verify email before password reset. */
	FORGOT_PASSWORD_VERIFY_EMAIL,

	/** JWT used to verify phone number before MPIN reset. */
	FORGOT_MPIN_VERIFY_PHONE,

	/** JWT used to verify email before MPIN reset. */
	FORGOT_MPIN_VERIFY_EMAIL,

	/** JWT used to send password reset OTP to phone. */
	FORGOT_PASSWORD_SEND_PHONE,

	/** JWT used to send MPIN reset OTP to phone. */
	FORGOT_MPIN_SEND_PHONE,

	// Post-Registration Setup

	/** JWT used to create the initial password after registration. */
	CREATE_PASSWORD,

	/** JWT used to set additional user details after registration. */
	SET_DETAILS,

	/** JWT used to set the initial MPIN after registration. */
	SET_MPIN,

	// Secure Access

	/** JWT required to access MPIN-protected endpoints. */
	ACCESS_MPIN,

	/** JWT required to access secured profile endpoints. */
	ACCESS_PROFILE,

	// Reset Actions

	/** JWT issued to reset an existing password. */
	RESET_PASSWORD,

	/** JWT issued to reset an existing MPIN. */
	RESET_MPIN
}
//END