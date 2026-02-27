package com.treasurex.userservice.model;

import java.time.LocalDateTime;

import com.treasurex.userservice.enums.OtpChannel;
import com.treasurex.userservice.enums.OtpPurpose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a system user. Includes authentication, OTP handling, verification
 * statuses, and user credentials. Suitable for MyBatis or other ORM usage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

	private Long id; // Primary key / internal DB id

	private String userType; // NORMAL_USER | BUSINESS_USER | ADVISOR_USER

	private String phoneNumber; // 10-digit unique phone number

	@Builder.Default
	private boolean isPhoneVerified = false; // Phone verification status

	private String passwordHash; // Hashed user password

	private String email; // Optional unique email

	@Builder.Default
	private boolean isEmailVerified = false; // Email verification status

	private String otp; // One-Time Password for verification

	private OtpPurpose otpPurpose; // OTP purpose (REGISTER, FORGOT_PASSWORD, etc.)

	private OtpChannel otpChannel; // OTP sent via PHONE or EMAIL

	private LocalDateTime otpExpiryTime; // OTP expiration timestamp

	@Builder.Default
	private int otpAttemptCount = 0; // Number of OTP attempts

	private String username; // Unique login username

	private String mpinHash; // Hashed MPIN
}
//END