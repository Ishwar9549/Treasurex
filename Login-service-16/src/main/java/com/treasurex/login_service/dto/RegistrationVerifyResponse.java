package com.treasurex.login_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationVerifyResponse {
	private String message;
	private String registrationToken; // short-lived token (REGISTRATION), may be null for some cases
}
