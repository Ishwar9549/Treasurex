package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base abstract exception for authentication failures. Thrown when a client
 * fails to provide valid credentials or authentication information is
 * missing/invalid. Maps to HTTP 401 (Unauthorized).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
public abstract class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected UnauthorizedException(String message) {
		super(message);
	}
}
//END