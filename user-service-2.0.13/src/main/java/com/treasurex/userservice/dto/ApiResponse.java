package com.treasurex.userservice.dto;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all endpoints. Supports both success and
 * error responses with optional metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private boolean success;
	private int statusCode;
	private String message;
	private T data;
	private Instant timestamp;

	/**
	 * Optional field-level or additional error details (used mainly for validation
	 * errors).
	 */
	private Map<String, String> errors;

	/* ===================== SUCCESS RESPONSES ===================== */

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder().success(true).statusCode(200).message(message).data(data)
				.timestamp(Instant.now()).build();
	}

	public static <T> ApiResponse<T> created(T data, String message) {
		return ApiResponse.<T>builder().success(true).statusCode(201).message(message).data(data)
				.timestamp(Instant.now()).build();
	}

	/* ===================== ERROR RESPONSES ===================== */

	public static <T> ApiResponse<T> error(int statusCode, String message) {
		return ApiResponse.<T>builder().success(false).statusCode(statusCode).message(message).timestamp(Instant.now())
				.build();
	}

	public static <T> ApiResponse<T> badRequest(String message) {
		return error(400, message);
	}

	public static <T> ApiResponse<T> unauthorized(String message) {
		return error(401, message);
	}

	public static <T> ApiResponse<T> forbidden(String message) {
		return error(403, message);
	}

	public static <T> ApiResponse<T> notFound(String message) {
		return error(404, message);
	}

	public static <T> ApiResponse<T> internalError(String message) {
		return error(500, message);
	}

	public static <T> ApiResponse<T> validationError(String message, Map<String, String> errors) {
		return ApiResponse.<T>builder().success(false).statusCode(400).message(message).errors(errors)
				.timestamp(Instant.now()).build();
	}

	public static <T> ApiResponse<T> conflict(String message) {
		return error(409, message);
	}
}
//END