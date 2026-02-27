package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for resending OTP to a user. Includes login identifier (email or
 * phone) and purpose of OTP.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReSendOtpRequest {

	@NotBlank(message = "Email / Phone cannot be blank")
	@Size(max = 100, message = "Login ID must not exceed 100 characters")
	@Pattern(regexp = "^(^[6-9]\\d{9}$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$)$", message = "Login ID must be a valid email or Indian phone number")
	private String loginId;

	@NotBlank(message = "Otp Purpose cannot be blank")
	@Pattern(regexp = "REGISTER|FORGOT_PASSWORD|FORGOT_MPIN", message = "Invalid otp Purpose type. Must be REGISTER or FORGOT_PASSWORD or FORGOT_MPIN")
	private String otpPurpose;

	@NotBlank(message = "Otp Chanell cannot be blank")
	@Pattern(regexp = "PHONE|EMAIL", message = "Invalid otp Channel type. Must be PHONE or EMAIL")
	private String otpChannel;
}
//END