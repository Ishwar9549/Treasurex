package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base exception for all client input and validation failures. Maps to HTTP 400
 * (Bad Request). Subclasses represent specific business rule violations.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public abstract class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadRequestException(String message) {
		super(message);
	}
}
//END