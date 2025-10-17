package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

	@NotBlank(message = "Address Line 1 is mandatory")
	@Size(max = 255, message = "Address Line 1 must be at most 255 characters")
	private String addressLine1;

	@Size(max = 255, message = "Address Line 2 must be at most 255 characters")
	private String addressLine2;

	@NotBlank(message = "City is mandatory")
	@Size(max = 100, message = "City must be at most 100 characters")
	private String city;

	@NotBlank(message = "State is mandatory")
	@Size(max = 100, message = "State must be at most 100 characters")
	private String state;

	@Size(max = 100, message = "District must be at most 100 characters")
	private String district;

	@NotBlank(message = "Postal Code is mandatory")
	@Size(max = 20, message = "Postal Code must be at most 20 characters")
	private String postalCode;

	@Pattern(regexp = "RESIDENTIAL|BUSINESS|OFFICE|BILLING|SHIPPING", message = "Invalid address type it must be (RESIDENTIAL | BUSINESS | OFFICE| BILLING| SHIPPING)")
	@NotBlank(message = "Address Type is mandatory")
	private String type;
}