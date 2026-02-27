package com.treasurex.userservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.treasurex.userservice.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the User Service. Converts exceptions into
 * standard API responses with proper HTTP status codes. Currently logs full
 * stack traces for development phase.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Helper method to build error responses using ApiResponse.
	 */
	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, String message) {
		ApiResponse<Void> response = ApiResponse.error(status.value(), message);
		return new ResponseEntity<>(response, status);
	}

	/**
	 * Handles all bad request runtime exceptions.
	 */
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
		log.warn("Bad request: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	/**
	 * Handles Resource Conflict Exception.
	 */
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	/**
	 * Handles Forbiden Exception.
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	/**
	 * Handles Forbiden Exception.
	 */
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	/**
	 * Handles NotFoundException when requested resource is not found.
	 */
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
		log.warn("Resource not found: ", ex);
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	/*
	 * Handles the JSON request body missing or invalid exception
	 */
	@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
			org.springframework.http.converter.HttpMessageNotReadableException ex) {

		log.warn("Request body missing or unreadable: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required and must be valid JSON");
	}

	/**
	 * Handles missing request parameters.
	 */
	@ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Void>> handleMissingRequestParam(
			org.springframework.web.bind.MissingServletRequestParameterException ex) {
		log.warn("Missing request parameter: ", ex);
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	/**
	 * Handles validation errors for @Valid annotated request bodies.
	 */
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
			org.springframework.web.bind.MethodArgumentNotValidException ex) {
		log.warn("Validation failed: ", ex);

		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

		ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder().success(false)
				.statusCode(HttpStatus.BAD_REQUEST.value()).message("Validation Failed").data(fieldErrors).build();

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(io.jsonwebtoken.JwtException.class)
	public ResponseEntity<ApiResponse<Void>> handleJwtException(io.jsonwebtoken.JwtException ex) {
		log.warn("JWT error: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
	}

	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
			org.springframework.security.access.AccessDeniedException ex) {
		log.warn("Access denied: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.FORBIDDEN, "Access denied - " + ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
		log.warn("Invalid request data: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}
}
//END