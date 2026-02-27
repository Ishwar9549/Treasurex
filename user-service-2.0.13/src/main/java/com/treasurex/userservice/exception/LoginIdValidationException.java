package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when Login ID is missing or invalid. Maps to HTTP 400 (Bad
 * Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class LoginIdValidationException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public LoginIdValidationException(String message) {
		super(message);
	}
}
//END