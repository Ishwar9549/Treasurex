package com.treasurex.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	@Size(max = 50, message = "First name must not exceed 50 characters")
	private String firstName;

	@NotBlank(message = "Last name cannot be blank")
	@Size(max = 50, message = "Last name must not exceed 50 characters")
	private String lastName;

	@NotBlank(message = "ARN Number is mandatory")
	@Size(max = 30, message = "ARN Number must not exceed 30 characters")
	private String arnNumber;

	@NotBlank(message = "Nominee Name is mandatory")
	@Size(max = 100, message = "Nominee Name must not exceed 100 characters")
	private String nomineeName;

	@NotBlank(message = "Nominee Contact Number is mandatory")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String nomineeContactNumber;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;

	@NotBlank(message = "User Name cannot be blank")
	@Size(min = 6, max = 30, message = "User Name must be between 4 and 30 characters")
	private String userName;

	@NotBlank(message = "Referral Code cannot be blank")
	@Size(max = 20, message = "Referral Code must not exceed 20 characters")
	private String referralCode;
}
// END
