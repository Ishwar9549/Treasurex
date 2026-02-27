package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the new password and confirm password values do not
 * match. Represents a client-side validation failure and maps to HTTP 400 (Bad
 * Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) /// 400
public class PasswordMismatchException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public PasswordMismatchException(String message) {
		super(message);
	}
}
//END