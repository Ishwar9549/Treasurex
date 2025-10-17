package com.treasurex.login_service.dto;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

	@NotBlank(message = "UserId cannot be blank")
	private String userId;

	@Pattern(regexp = "NORMAL_USER|BUSINESS_USER|ADVISOR_USER", message = "Invalid role it must be (NORMAL_USER | BUSINESS_USER | ADVISOR_USER)")
	@NotBlank(message = "User role cannot be blank")
	private String role;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password cannot be blank")
	private String password;

	@NotBlank(message = "First name cannot be blank")
	private String firstName;

	private String middleName;

	@NotBlank(message = "Last name cannot be blank")
	private String lastName;

	@NotBlank(message = "Phone number cannot be blank")
	private String phoneNumber;

	@Column(length = 10)
	private String alternativePhoneNumber;

	@Email(message = "Invalid alternative email format")
	private String alternativeEmail;

	private LocalDate dob;

	@Valid
	@NotNull(message = "Address cannot be null")
	private AddressRequest address;

	@Pattern(regexp = "Aadhar|Passport|Pan", message = "Invalid ID type")
	private String idType;
	private String idNumber;

	private List<SecurityQuestionRequest> securityQuestions;
}