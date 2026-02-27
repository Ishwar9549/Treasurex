package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
	private String loginId;

	@NotBlank(message = "Otp Purpose cannot be blank")
	@Pattern(regexp = "REGISTER_PHONE|REGISTER_EMAIL|FORGOT_PASSWORD_EMAIL|FORGOT_PASSWORD_PHONE|FORGOT_MPIN_EMAIL|FORGOT_MPIN_PHONE|FORGOT_PASSWORD|FORGOT_MPIN", message = "Invalid type. Must be 'REGISTER_PHONE' or 'REGISTER_EMAIL' or FORGOT_PASSWORD_EMAIL or "
			+ "FORGOT_PASSWORD_PHONE or FORGOT_MPIN_EMAIL " + "or FORGOT_MPIN_PHONE")
	private String otpPurpose;
}
//END