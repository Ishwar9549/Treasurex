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
 * standard API responses with proper HTTP status codes.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// Helper method to build standardized error responses
	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, String message) {
		ApiResponse<Void> response = ApiResponse.error(status.value(), message);
		return new ResponseEntity<>(response, status);
	}

	// Handles all BadRequestException (HTTP 400)
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
		log.warn("Bad request: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	// Handles resource conflicts (HTTP 409)
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
		log.warn("Conflict: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	// Handles unauthorized access (HTTP 401)
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
		log.warn("Unauthorized: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	// Handles forbidden access (HTTP 403)
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
		log.warn("Forbidden: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	// Handles not found resources (HTTP 404)
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
		log.warn("Not Found: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	// Handles missing or invalid JSON body in request
	@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
			org.springframework.http.converter.HttpMessageNotReadableException ex) {
		log.warn("Request body missing or invalid JSON: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required and must be valid JSON");
	}

	// Handles missing request parameters
	@ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Void>> handleMissingRequestParam(
			org.springframework.web.bind.MissingServletRequestParameterException ex) {
		log.warn("Missing request parameter: {}", ex.getParameterName());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	// Handles validation errors from @Valid request bodies
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
			org.springframework.web.bind.MethodArgumentNotValidException ex) {

		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

		log.warn("Validation failed: {}", fieldErrors);

		ApiResponse<Map<String, String>> response = ApiResponse.validationError("Validation Failed", fieldErrors);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// Handles JWT exceptions (invalid or expired tokens)
	@ExceptionHandler(io.jsonwebtoken.JwtException.class)
	public ResponseEntity<ApiResponse<Void>> handleJwtException(io.jsonwebtoken.JwtException ex) {
		log.warn("JWT error: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
	}

	// Handles Spring Security access denied exceptions
	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
			org.springframework.security.access.AccessDeniedException ex) {
		log.warn("Access denied: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.FORBIDDEN, "Access denied - " + ex.getMessage());
	}

	// Handles generic IllegalArgumentException
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
		log.warn("Invalid request data: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}
}
//END