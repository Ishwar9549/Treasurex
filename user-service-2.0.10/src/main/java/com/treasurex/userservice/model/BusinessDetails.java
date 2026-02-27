package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BusinessDetails entity representing the business-related information of a
 * user. Linked to the User entity via userId. Suitable for use with MyBatis.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDetails {

	private Long id; // Internal DB id

	private String fullName; // Full name of the business owner

	private String businessName; // Name of the business

	private String businessPhone; // Business contact number (10 digits)

	private String businessPlace; // Location or address of the business

	private String panNumber; // PAN number of the business

	private String gstNumber; // GST number of the business

	private String nomineeName; // Nominee's full name

	private String nomineeContactNumber; // Nominee's contact number

	private String bankName; // Name of the bank for business account

	private String accountNumber; // Bank account number

	private String ifscCode; // IFSC code for the bank branch

	private String businessWebSite; // Website URL of the business

	private String businessType; // Type of business (e.g., Retail, Services, etc.)

	private String bio; // Short bio or description about the business

	private Long userId; // Reference to User entity
}
//END