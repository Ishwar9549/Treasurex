package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a phone number is already registered. Maps to HTTP 409
 * (Conflict).
 */
@ResponseStatus(HttpStatus.CONFLICT) // 409
public class PhoneAlreadyRegisteredException extends ConflictException {

	private static final long serialVersionUID = 1L;

	public PhoneAlreadyRegisteredException(String message) {
		super(message);
	}
}
//END
