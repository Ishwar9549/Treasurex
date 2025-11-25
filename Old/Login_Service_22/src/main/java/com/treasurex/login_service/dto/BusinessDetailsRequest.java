package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDetailsRequest {

	@NotBlank(message = "Business Name is mandatory")
	private String businessName;

	@NotBlank(message = "Business Phone is mandatory")
	private String businessPhone;

	@NotBlank(message = "Business Place is mandatory")
	private String businessPlace;

	@NotBlank(message = "PAN Number is mandatory")
	private String panNumber;

	@NotBlank(message = "GST Number is mandatory")
	private String gstNumber;

	@NotBlank(message = "Nominee Name is mandatory")
	private String nomineeName;

	@NotBlank(message = "Nominee Contact Number is mandatory")
	private String nomineeContactNumber;

	@NotBlank(message = "Bank Name is mandatory")
	private String bankName;

	@NotBlank(message = "Account Number is mandatory")
	private String accountNumber;

	@NotBlank(message = "ifsc Code is mandatory")
	private String ifscCode;

	@NotBlank(message = "bio is mandatory")
	private String bio;

}
//END
