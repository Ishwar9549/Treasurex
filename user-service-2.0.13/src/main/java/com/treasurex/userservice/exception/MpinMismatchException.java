package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when MPIN and Confirm MPIN do not match. Represents a
 * validation failure and maps to HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class MpinMismatchException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public MpinMismatchException(String message) {
		super(message);
	}
}
///END