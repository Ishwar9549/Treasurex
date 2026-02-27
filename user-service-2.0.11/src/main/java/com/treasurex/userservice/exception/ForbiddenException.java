package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base exception for authorization and access-related failures. Thrown when the
 * user is authenticated but not allowed to perform the requested operation.
 * Maps to HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // 403
public abstract class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected ForbiddenException(String message) {
		super(message);
	}
}
//END