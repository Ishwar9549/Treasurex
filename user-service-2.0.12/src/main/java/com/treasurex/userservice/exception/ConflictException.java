package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource conflicts with existing data (HTTP
 * 409)
 */
@ResponseStatus(HttpStatus.CONFLICT) // 409
public abstract class ConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConflictException(String message) {
		super(message);
	}
}
//END