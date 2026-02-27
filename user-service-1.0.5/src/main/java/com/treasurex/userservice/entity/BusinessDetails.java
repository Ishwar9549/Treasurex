package com.treasurex.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "business_details")
public class BusinessDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore // avoid accidental exposure in API responses
	private Long id;

	// Full name of the business owner
	private String fullName;

	// Name of the business
	private String businessName;

	// Business contact number (10 digits)
	private String businessPhone;

	// Location or address of the business
	private String businessPlace;

	// PAN number of the business
	private String panNumber;

	// GST number of the business
	private String gstNumber;

	// Nominee's full name
	private String nomineeName;

	// Nominee's contact number
	private String nomineeContactNumber;

	// Name of the bank for business account
	private String bankName;

	// Bank account number
	private String accountNumber;

	// IFSC code for the bank branch
	private String ifscCode;

	// Website URL of the business
	private String businessWebSite;

	// Type of business (e.g., Retail, Services, etc.)
	private String businessType;

	// Short bio or description about the business
	private String bio;

	// Reference to the main User entity (one-to-one relationship)
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
	private User user;
	
}
//END
