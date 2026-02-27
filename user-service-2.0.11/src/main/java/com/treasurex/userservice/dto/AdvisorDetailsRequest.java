package com.treasurex.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for capturing advisor onboarding details. Used during advisor
 * registration and profile creation flows.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisorDetailsRequest {

	@NotBlank(message = "First name cannot be blank")
	private String firstName;

	@NotBlank(message = "Last name cannot be blank")
	private String lastName;

	@NotBlank(message = "ARN Number is mandatory")
	private String arnNumber;

	@NotBlank(message = "Nominee Name is mandatory")
	private String nomineeName;

	@NotBlank(message = "Nominee Contact Number is mandatory")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String nomineeContactNumber;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "User Name cannot be blank")
	private String userName;

	@NotBlank(message = "Referral Code cannot be blank")
	private String referralCode;
}
//END