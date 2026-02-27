package com.treasurex.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for retrieving a forgotten username. Accepts either email or
 * phone number.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RememberUserNameRequest {

	@NotBlank(message = "Email / Phone cannot be blank")
	private String loginId;

}
//END