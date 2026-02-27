package com.treasurex.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a referral code provided by the client is invalid. Maps
 * to HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class ReferralCodeValidationException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public ReferralCodeValidationException(String message) {
		super(message);
	}
}
//END