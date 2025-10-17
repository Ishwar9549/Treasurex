package com.treasurex.login_service.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
	private String status;
	private String message;
	private String nextStep;
	private String token;
	private String userId;
	private String error;

	// Flexible extra data (optional)
	private Map<String, Object> data;
}
