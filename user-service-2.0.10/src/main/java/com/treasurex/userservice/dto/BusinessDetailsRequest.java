package com.treasurex.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for capturing business user onboarding details, including
 * business, nominee, and banking information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDetailsRequest {

	@NotBlank(message = "Full name cannot be blank")
	private String fullName;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Business Name is mandatory")
	private String businessName;

	@NotBlank(message = "Business Phone is mandatory")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String businessPhone;

	@NotBlank(message = "Business Place is mandatory")
	private String businessPlace;

	@NotBlank(message = "PAN Number is mandatory")
	private String panNumber;

	@NotBlank(message = "GST Number is mandatory")
	private String gstNumber;

	@NotBlank(message = "Nominee Name is mandatory")
	private String nomineeName;

	@NotBlank(message = "Nominee Contact Number is mandatory")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String nomineeContactNumber;

	@NotBlank(message = "Bank Name is mandatory")
	private String bankName;

	@NotBlank(message = "Account Number is mandatory")
	private String accountNumber;

	@NotBlank(message = "IFSC Code is mandatory")
	private String ifscCode;

	@NotBlank(message = "Business Website is mandatory")
	private String businessWebSite;

	@NotBlank(message = "Business Type is mandatory")
	private String businessType;

	@NotBlank(message = "Bio is mandatory")
	private String bio;

	@NotBlank(message = "User Name cannot be blank")
	private String userName;

	@NotBlank(message = "Referral Code cannot be blank")
	private String referralCode;
}
//END