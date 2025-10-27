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
public class MpinRequest {

	@NotBlank(message = "mpin cannot be blank")
	private String mpin;

	@NotBlank(message = "confirmMpin cannot be blank")
	private String confirmMpin;
}
//END 
