package com.treasurex.payment_gateway.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all end points Supports success and error
 * responses
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private boolean success; // true or false
	private int statusCode; // HTTP code (200, 400, etc.)
	private String message; // success or error message
	private T data; // actual response body
	private Instant timestamp; // when response was created

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder().success(true).statusCode(200).message(message).data(data)
				.timestamp(Instant.now()).build();
	}

	public static <T> ApiResponse<T> created(T data, String message) {
		return ApiResponse.<T>builder().success(true).statusCode(201).message(message).data(data)
				.timestamp(Instant.now()).build();
	}

	public static <T> ApiResponse<T> error(int statusCode, String message) {
		return ApiResponse.<T>builder().success(false).statusCode(statusCode).message(message).timestamp(Instant.now())
				.build();
	}
}
//END