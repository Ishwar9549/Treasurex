package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a token is missing a required purpose. Maps to HTTP 400
 * (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class InvalidTokenPurposeException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public InvalidTokenPurposeException(String message) {
		super(message);
	}
}
//END