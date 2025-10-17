package com.treasurex.login_service.dto;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

	@NotBlank(message = "UserId cannot be blank")
	@Column(nullable = false, unique = true)
	private String userId;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Password cannot be blank")
	@Column(nullable = false, length = 3)
	private String password;

	@NotBlank(message = "First name cannot be blank")
	@Column(nullable = false)
	private String firstName;

	private String middleName;

	@NotBlank(message = "Last name cannot be blank")
	@Column(nullable = false)
	private String lastName;

	@NotBlank(message = "Phone number cannot be blank")
	@Column(nullable = false, length = 10)
	private String phoneNumber;

	@Column(length = 10)
	private String alternativePhoneNumber;

	@Email(message = "Invalid alternative email format")
	private String alternativeEmail;

	private LocalDate dob;

	@Column(length = 500)
	private String address;

	private String idType; // e.g., Aadhar, Passport, PAN
	private String idNumber;
}