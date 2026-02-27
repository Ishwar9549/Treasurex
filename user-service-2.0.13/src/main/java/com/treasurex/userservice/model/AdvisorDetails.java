package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents personal and professional details of an advisor user. Linked to
 * the main User entity via userId. Suitable for MyBatis or other ORM usage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisorDetails {

	private Long id; // Primary key / unique DB id

	private Long userId; // Reference to the User entity (foreign key)

	private String firstName; // Advisor's first name

	private String lastName; // Advisor's last name

	private String arnNumber; // AMFI Registration Number (ARN) for the advisor

	private String nomineeName; // Nominee's full name

	private String nomineeContactNumber; // Nominee's contact number
}
//END