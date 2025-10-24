package com.treasurex.login_service.exception;

/**
 * Exception thrown when a requested resource is Invalid 
 */
public class InvalidCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidCredentialsException(String message) {
		super(message);
	}
}
//END  