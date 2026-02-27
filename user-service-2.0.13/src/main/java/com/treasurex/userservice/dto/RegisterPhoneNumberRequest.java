package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for registering a user via phone number. Includes user type and
 * phone number information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterPhoneNumberRequest {

	@NotBlank(message = "Type of User cannot be blank")
	@Pattern(regexp = "^(NORMAL_USER|BUSINESS_USER|ADVISOR_USER)$", message = "Type of User must be NORMAL_USER, BUSINESS_USER or ADVISOR_USER")
	private String typeOfUser;

	@NotBlank(message = "Phone number cannot be blank")
	@Size(min = 10, max = 10, message = "Phone number must be 10 digits")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String phoneNumber;
}
// END
