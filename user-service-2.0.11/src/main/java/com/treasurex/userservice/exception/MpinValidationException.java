package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when MPIN validation fails (e.g., new MPIN same as old
 * MPIN). Maps to HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class MpinValidationException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public MpinValidationException(String message) {
		super(message);
	}
}
//END