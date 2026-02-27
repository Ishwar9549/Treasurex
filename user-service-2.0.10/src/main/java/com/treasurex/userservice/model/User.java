package com.treasurex.userservice.model;

import java.time.LocalDateTime;

import com.treasurex.userservice.dto.OtpPurpose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User entity representing a system user. Includes phone/email verification,
 * OTP handling, username, and MPIN. Suitable for use with MyBatis.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

	private Long id; // internal DB id

	private String typeOfUser; // NORMAL_USER | BUSINESS_USER | ADVISOR_USER

	private String phoneNumber; // User's phone number (10 digits, unique)

	@Builder.Default
	private boolean phoneVerified = false; // Phone verification status

	private String password; // Hashed password

	private String email; // Optional email (unique if present)

	@Builder.Default
	private boolean emailVerified = false; // Email verification status

	private String otp; // OTP sent for verification

	private OtpPurpose otpPurpose; // Purpose of the OTP

	private LocalDateTime otpExpiry; // OTP expiry timestamp

	@Builder.Default
	private int otpAttempts = 0; // Number of OTP attempts

	private String userName; // Unique username

	private String mpinHash; // Hashed MPIN
}
//END