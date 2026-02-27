package com.treasurex.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating user profile details. Includes basic
 * personal information and referral code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsRequest {

	@NotBlank(message = "First name cannot be blank")
	private String firstName;

	@NotBlank(message = "Last name cannot be blank")
	private String lastName;

	@NotBlank(message = "User Name cannot be blank")
	private String userName;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Referral Code cannot be blank")
	private String referralCode;
}
//END