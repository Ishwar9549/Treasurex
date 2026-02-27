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
@Table(name = "user_details")
public class UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore // avoid exposing internal DB id in API responses
	private Long id;

	// User's first name
	private String firstName;

	// User's last name
	private String lastName;

	// Reference to the main User entity (one-to-one)
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
	private User user;

}
//END