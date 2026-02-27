package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AdvisorDetails entity representing personal and professional details of an
 * advisor user. Linked to the User entity via userId. Suitable for use with
 * MyBatis.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisorDetails {

	private Long id; // Unique DB id

	private String firstName; // Advisor's first name

	private String lastName; // Advisor's last name

	private String arnNumber; // ARN (AMFI Registration Number) for the advisor

	private String nomineeName; // Nominee's full name

	private String nomineeContactNumber; // Nominee's contact number

	private Long userId; // Reference back to User entity
}
//END