package com.treasurex.userservice.dto;

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

	@NotBlank(message = "New Mpin cannot be blank")
	private String newMpin;

	@NotBlank(message = "confirm Mpin cannot be blank")
	private String confirmMpin;

}
//END 
