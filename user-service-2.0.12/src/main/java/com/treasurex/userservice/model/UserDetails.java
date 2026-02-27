package com.treasurex.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserDetails entity representing personal details of a user. Linked to the
 * User entity via userId. Suitable for use with MyBatis.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {

	private Long id; // Internal DB id

	private String firstName; // User's first name

	private String lastName; // User's last name

	private Long userId; // Reference to User entity
}
//END