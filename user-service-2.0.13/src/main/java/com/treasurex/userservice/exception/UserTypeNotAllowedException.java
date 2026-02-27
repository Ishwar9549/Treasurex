package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to perform an operation that is
 * restricted to a specific user type, but their type does not have permission
 * to perform it.
 * 
 * This represents a forbidden action and maps to HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // 403
public class UserTypeNotAllowedException extends ForbiddenException {

	private static final long serialVersionUID = 1L;

	public UserTypeNotAllowedException(String message) {
		super(message);
	}
}
//END