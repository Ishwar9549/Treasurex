package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents business-related information of a user. Linked to the main User
 * entity via userId. Suitable for MyBatis or other ORM usage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDetails {

	private Long id; // Primary key / unique DB id

	private Long userId; // Reference to the User entity (foreign key)

	// Owner / Personal Info
	private String fullName; // Full name of the business owner

	// Business Info
	private String businessName; // Name of the business
	private String businessType; // Type of business (e.g., Retail, Services)
	private String businessPhone; // Business contact number (10 digits)
	private String businessPlace; // Business location / address
	private String businessWebSite; // Website URL
	private String bio; // Short bio or description about the business

	// Tax Info
	private String panNumber; // PAN number
	private String gstNumber; // GST number

	// Nominee Info
	private String nomineeName; // Nominee's full name
	private String nomineeContactNumber; // Nominee's contact number

	// Banking Info
	private String bankName; // Bank name
	private String accountNumber; // Account number
	private String ifscCode; // IFSC code
}
//END