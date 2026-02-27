package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to perform an operation that requires a
 * verified phone number, but the phone number has not yet been verified. Maps
 * to HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 403
public class PhoneNotVerifiedException extends UnauthorizedException {

	private static final long serialVersionUID = 1L;

	public PhoneNotVerifiedException(String message) {
		super(message);
	}
}
//END