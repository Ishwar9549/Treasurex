package com.treasurex.login_service.dto;

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
public class RegisterStartRequest {

	@Pattern(regexp = "NORMAL_USER|BUSINESS_USER|ADVISOR_USER", message = "Invalid Type of user. Must be NORMAL_USER, BUSINESS_USER or ADVISOR_USER")
	@NotBlank(message = "Type of User cannot be blank")
	private String typeOfUser;

	@NotBlank(message = "UserId cannot be blank")
	private String userId;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String phoneNumber;

	@NotBlank(message = "Password cannot be blank")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character")
	private String password;

	@NotBlank(message = "Confirm Password cannot be blank")
	private String confirmPassword;
}
//END