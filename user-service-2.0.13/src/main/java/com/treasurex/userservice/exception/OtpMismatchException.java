package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the provided OTP does not match the stored value.
 * Represents a client-side validation failure and maps to HTTP 400 (Bad
 * Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) /// 400
public class OtpMismatchException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public OtpMismatchException(String message) {
		super(message);
	}
}
//END