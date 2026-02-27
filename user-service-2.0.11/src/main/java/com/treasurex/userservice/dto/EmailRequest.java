package com.treasurex.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for initiating email-based recovery flows such as password or
 * MPIN reset.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "purpose cannot be blank")
	@Pattern(regexp = "FORGOT_PASSWORD_EMAIL|FORGOT_PASSWORD_PHONE|FORGOT_MPIN_EMAIL|FORGOT_MPIN_PHONE", message = "Invalid type. Must be 'FORGOT_PASSWORD_EMAIL' or 'FORGOT_PASSWORD_PHONE' or 'FORGOT_MPIN_EMAIL' or 'FORGOT_MPIN_PHONE'")
	private String purpose;
}
//END
