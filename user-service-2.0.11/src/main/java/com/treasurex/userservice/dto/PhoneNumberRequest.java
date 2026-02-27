package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for initiating recovery flows using a phone number. Used for OTP
 * verification when recovering password or MPIN.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneNumberRequest {

	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String phoneNumber;

	@NotBlank(message = "purpose cannot be blank")
	@Pattern(regexp = "FORGOT_PASSWORD_EMAIL|FORGOT_PASSWORD_PHONE|FORGOT_MPIN_EMAIL|FORGOT_MPIN_PHONE", message = "Invalid type. Must be 'FORGOT_PASSWORD_EMAIL' or 'FORGOT_PASSWORD_PHONE' or 'FORGOT_MPIN_EMAIL' or 'FORGOT_MPIN_PHONE'")
	private String purpose;

}
//END