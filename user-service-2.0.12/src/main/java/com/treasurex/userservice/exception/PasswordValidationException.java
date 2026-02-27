package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when password validation fails (e.g., new password same as
 * old, weak password, etc.). Maps to HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class PasswordValidationException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public PasswordValidationException(String message) {
		super(message);
	}
}
//END