package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the phone number provided does not match the logged-in
 * user's phone number. Maps to HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // 403
public class PhoneMismatchException extends ForbiddenException {

	private static final long serialVersionUID = 1L;

	public PhoneMismatchException(String message) {
		super(message);
	}
}
//END