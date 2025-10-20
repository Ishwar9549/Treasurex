package com.treasurex.app_config.dto;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Standard API Response Wrapper. Used across all end points for consistent
 * JSON structure.
 */
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
	private Map<String, Object> data; // Flexible data pay load
}
