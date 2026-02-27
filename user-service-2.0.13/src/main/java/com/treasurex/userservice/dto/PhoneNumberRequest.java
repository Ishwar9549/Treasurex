package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	@Size(min = 10, max = 10, message = "Phone number must be 10 digits")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String phoneNumber;

	@NotBlank(message = "Purpose cannot be blank")
	@Pattern(regexp = "^(FORGOT_PASSWORD|FORGOT_MPIN)$", message = "Purpose must be FORGOT_PASSWORD or FORGOT_MPIN")
	private String purpose;
}
// END
