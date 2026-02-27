package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to register or assign a user ID that
 * already exists in the system. Represents a uniqueness constraint violation
 * and maps to HTTP 409 (Conflict).
 */
@ResponseStatus(HttpStatus.CONFLICT) // 409
public class UserIdAlreadyTakenException extends ConflictException {

	private static final long serialVersionUID = 1L;

	public UserIdAlreadyTakenException(String message) {
		super(message);
	}
}
//END