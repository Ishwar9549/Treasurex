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
 * Request DTO for capturing business user onboarding details, including
 * business, nominee, and banking information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDetailsRequest {

	@NotBlank(message = "Full name cannot be blank")
	@Size(max = 100, message = "Full name must not exceed 100 characters")
	private String fullName;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	private String email;

	@NotBlank(message = "Business Name is mandatory")
	@Size(max = 150, message = "Business Name must not exceed 150 characters")
	private String businessName;

	@NotBlank(message = "Business Phone is mandatory")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String businessPhone;

	@NotBlank(message = "Business Place is mandatory")
	@Size(max = 150, message = "Business Place must not exceed 150 characters")
	private String businessPlace;

	@NotBlank(message = "PAN Number is mandatory")
	private String panNumber;

	@NotBlank(message = "GST Number is mandatory")
	private String gstNumber;

	@NotBlank(message = "Nominee Name is mandatory")
	@Size(max = 100, message = "Nominee Name must not exceed 100 characters")
	private String nomineeName;

	@NotBlank(message = "Nominee Contact Number is mandatory")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String nomineeContactNumber;

	@NotBlank(message = "Bank Name is mandatory")
	@Size(max = 100, message = "Bank Name must not exceed 100 characters")
	private String bankName;

	@NotBlank(message = "Account Number is mandatory")
	private String accountNumber;

	@NotBlank(message = "IFSC Code is mandatory")
	private String ifscCode;

	@NotBlank(message = "Business Website is mandatory")
	private String businessWebSite;

	@NotBlank(message = "Business Type is mandatory")
	@Size(max = 100, message = "Business Type must not exceed 100 characters")
	private String businessType;

	@NotBlank(message = "Bio is mandatory")
	@Size(max = 500, message = "Bio must not exceed 500 characters")
	private String bio;

	@NotBlank(message = "User Name cannot be blank")
	@Size(min = 6, max = 30, message = "User Name must be between 4 and 30 characters")
	private String userName;

	@NotBlank(message = "Referral Code cannot be blank")
	@Size(max = 20, message = "Referral Code must not exceed 20 characters")
	private String referralCode;
}
// END
