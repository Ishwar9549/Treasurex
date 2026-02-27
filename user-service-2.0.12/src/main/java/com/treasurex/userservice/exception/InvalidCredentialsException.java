package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is Invalid
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
public class InvalidCredentialsException extends UnauthorizedException {

	private static final long serialVersionUID = 1L;

	public InvalidCredentialsException(String message) {
		super(message);

	}
}
//END  