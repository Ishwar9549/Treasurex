package com.treasurex.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReSendOtpRequest {

	@NotBlank(message = "Login ID (User ID / Email / Phone) cannot be blank")
	private String loginId;

	@Pattern(regexp = "Account-Verification|Password-Reset", message = "Invalid verification type it must be (Account-Verification | Password-Reset)")
	@NotBlank(message = "Otp for cannot be blank")
	private String otpFor;
}
//END