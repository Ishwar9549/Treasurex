package com.treasurex.login_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordBySecurityQuestionRequest {
    private String userId;
    private String newPassword;
    private String securityAnswer;
}
