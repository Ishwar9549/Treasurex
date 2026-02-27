package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for verifying a one-time password (OTP). Used during registration
 * and recovery flows.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerifyRequest {

	@NotBlank(message = "OTP cannot be blank")
	private String otp;
}
//END