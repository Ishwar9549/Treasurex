package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents personal details of a user. Linked to the main User entity via
 * userId. Suitable for MyBatis or other ORM usage.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {

	private Long id; // Primary key / internal DB id

	private Long userId; // Reference to the User entity (foreign key)

	private String firstName; // User's first name

	private String lastName; // User's last name
}
//END