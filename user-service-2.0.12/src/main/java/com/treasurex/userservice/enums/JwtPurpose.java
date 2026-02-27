package com.treasurex.userservice.enums;

/**
 * Defines the purpose of a JWT token in the UserService system. Used to
 * differentiate tokens for registration, password reset, MPIN, and profile
 * access.
 */
public enum JwtPurpose {

	// Registration related
	REGISTER_EMAIL, // JWT sent for email registration
	REGISTER_VERIFY_PHONE, // JWT to verify phone during registration
	REGISTER_VERIFY_EMAIL, // JWT to verify email during registration

	// Password / MPIN recovery
	FORGOT_PHONE, // JWT for initiating phone-based recovery
	FORGOT_PASSWORD_VERIFY_PHONE, // JWT to verify phone for password reset
	FORGOT_PASSWORD_VERIFY_EMAIL, // JWT to verify email for password reset
	FORGOT_MPIN_VERIFY_PHONE, // JWT to verify phone for MPIN reset
	FORGOT_MPIN_VERIFY_EMAIL, // JWT to verify email for MPIN reset

	// User setup after registration
	CREATE_PASSWORD, // JWT for creating initial password
	SET_DETAILS, // JWT for setting user details after registration
	SET_MPIN, // JWT for setting initial MPIN

	// Access and authentication
	ACCESS_MPIN, // JWT for accessing MPIN-protected endpoints
	ACCESS_PROFILE, // JWT for accessing user profile

	// Reset actions
	RESET_PASSWORD, // JWT for resetting password
	RESET_MPIN // JWT for resetting MPIN
}
//END