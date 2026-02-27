package com.treasurex.userservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles all generic runtime exceptions
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
		log.error("RuntimeException occurred: ", ex);
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", "Internal Server Error");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handles ResourceNotFoundException when requested resource is not found
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
		log.warn("Resource not found: ", ex);
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", "Not Found");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles InvalidCredentialsException when login or authentication fails
	 */
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String, Object>> invalidCredentialsException(InvalidCredentialsException ex) {
		log.warn("Invalid credentials: ", ex);
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Bad Request");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles validation errors for @Valid annotated request bodies
	 */
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(
			org.springframework.web.bind.MethodArgumentNotValidException ex) {
		log.warn("Validation failed: ", ex);
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Validation Failed");

		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
		body.put("messages", fieldErrors);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles missing request parameters
	 */
	@ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
	public ResponseEntity<Map<String, Object>> missingServletRequestParameterException(
			org.springframework.web.bind.MissingServletRequestParameterException ex) {
		log.warn("Missing request parameter: ", ex);
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Bad Request");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}
//END