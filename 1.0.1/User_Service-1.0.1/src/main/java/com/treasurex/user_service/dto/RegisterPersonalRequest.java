package com.treasurex.user_service.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterPersonalRequest {

	@NotBlank(message = "Login ID (User ID / Email / Phone) cannot be blank")
	private String loginId;

	@NotBlank(message = "First name cannot be blank")
	private String firstName;

	private String middleName;

	@NotBlank(message = "Last name cannot be blank")
	private String lastName;

	private String alternativePhoneNumber;

	@Email(message = "Invalid alternative email format")
	private String alternativeEmail;

	private LocalDate dob;

	@Pattern(regexp = "Aadhar|Passport|Pan", message = "Invalid Govt ID type it must be (Aadhar | Passport | Pan)")
	@NotBlank(message = "Govt Id type cannot be blank it must be (Aadhar | Passport | Pan)")
	private String govtIdType;

	private String govtIdNumber;

}
//END