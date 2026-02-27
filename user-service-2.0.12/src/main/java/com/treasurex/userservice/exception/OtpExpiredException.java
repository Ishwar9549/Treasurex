package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the provided OTP has expired. Indicates a client-side
 * validation failure and maps to HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) /// 400
public class OtpExpiredException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public OtpExpiredException(String message) {
		super(message);
	}
}
//END