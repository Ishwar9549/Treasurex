package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the provided MPIN is invalid, e.g., not exactly 4
 * digits. Maps to HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class InvalidMpinException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public InvalidMpinException(String message) {
		super(message);
	}
}
//END