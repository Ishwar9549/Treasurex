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
public class ResetMpinRequest {

	@NotBlank(message = "Password cannot be blank")
	private String password;

	@NotBlank(message = "new MPIN cannot be blank")
	private String newMpin;

	@NotBlank(message = "Confirm MPIN cannot be blank")
	private String confirmMpin;;

}
//END