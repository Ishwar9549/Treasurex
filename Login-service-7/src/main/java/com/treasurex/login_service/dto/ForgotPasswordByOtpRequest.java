package com.treasurex.login_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordByOtpRequest  {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;
    
}