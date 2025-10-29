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
public class AdvisorDetailsRequest {

	@NotBlank(message = "ARN Number is mandatory")
	private String arnNumber;

	@NotBlank(message = "Nominee Name is mandatory")
	private String nomineeName;

	@NotBlank(message = "Nominee Contact Number is mandatory")
	private String nomineeContactNumber;

	@NotBlank(message = "Certification Id is mandatory")
	private String certificationId;

	@NotBlank(message = "Experience Years is mandatory")
	private String experienceYears;
}
//END 