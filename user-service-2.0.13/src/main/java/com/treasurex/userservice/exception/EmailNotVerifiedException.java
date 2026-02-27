package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to perform an operation that requires a
 * verified email, but the email has not yet been verified. Represents an
 * unauthorized action and maps to HTTP 401 (Unauthorized).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
public class EmailNotVerifiedException extends UnauthorizedException {
	private static final long serialVersionUID = 1L;

	public EmailNotVerifiedException(String message) {
		super(message);
	}
}
//END