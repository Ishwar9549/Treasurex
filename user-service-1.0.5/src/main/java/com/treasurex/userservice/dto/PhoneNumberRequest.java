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
public class PhoneNumberRequest {

	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
	private String phoneNumber;
	
	@NotBlank(message = "purpose cannot be blank")
	@Pattern(regexp = "FORGOT_PASSWORD|FORGOT_MPIN", message = "Invalid type. Must be or 'FORGOT_PASSWORD' or 'FORGOT_MPIN'")
	private String purpose;

}
//END