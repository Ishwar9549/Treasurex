package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNameCheckRequest {

	@NotBlank(message = "username cannot be blank")
	@Size(min = 6, message = "username must be above 6 Char  ")
	private String userName;

}
//END