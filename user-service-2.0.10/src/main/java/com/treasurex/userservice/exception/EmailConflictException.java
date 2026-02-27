package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to register or update a user account
 * with an email address that already exists in the system. Represents a
 * uniqueness constraint violation and maps to HTTP 409 (Conflict).
 */

@ResponseStatus(HttpStatus.CONFLICT) // 409
public class EmailConflictException extends ConflictException {

	private static final long serialVersionUID = 1L;

	public EmailConflictException(String message) {
		super(message);
	}
}
//END