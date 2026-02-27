package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the OTP purpose is missing or does not match the
 * expected purpose for the current operation. Represents a client-side
 * validation error and maps to HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class InvalidOtpPurposeException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public InvalidOtpPurposeException(String message) {
		super(message);
	}
}
//END 
