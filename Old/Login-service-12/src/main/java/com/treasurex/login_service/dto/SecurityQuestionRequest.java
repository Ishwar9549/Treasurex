package com.treasurex.login_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityQuestionRequest {
	
    private String question;
    private String answer; 
}