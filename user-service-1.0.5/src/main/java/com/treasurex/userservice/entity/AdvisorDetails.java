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
@Table(name = "advisor_details")
public class AdvisorDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore // avoid accidental exposure in API responses
	private Long id;

	// Advisor's first name
	private String firstName;

	// Advisor's last name
	private String lastName;

	// ARN (AMFI Registration Number) for the advisor
	private String arnNumber;

	// Nominee's full name
	private String nomineeName;

	// Nominee's contact number
	private String nomineeContactNumber;

	// Reference to the main User entity (one-to-one relationship)
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
	private User user;

}
//END