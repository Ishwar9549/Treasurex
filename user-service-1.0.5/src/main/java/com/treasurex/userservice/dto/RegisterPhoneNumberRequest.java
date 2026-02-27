package com.treasurex.userservice.dto;

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
public class RegisterPhoneNumberRequest {

	@Pattern(regexp = "NORMAL_USER|BUSINESS_USER|ADVISOR_USER", message = "Invalid Type of user. Must be NORMAL_USER, BUSINESS_USER or ADVISOR_USER")
	@NotBlank(message = "Type of User cannot be blank")
	private String typeOfUser;

	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter 10 digit number")
	private String phoneNumber;

}
//END