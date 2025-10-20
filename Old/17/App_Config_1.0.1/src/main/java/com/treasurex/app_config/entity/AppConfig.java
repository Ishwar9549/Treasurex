package com.treasurex.app_config.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keyName; // Example: "MAX_LOGIN_ATTEMPTS" or "MAX_OTP_Resend" etc 

    @Column(nullable = false)
    private String value; // Stored as String for flexibility

    private String description; // Optional: explain what this config does

    private String type; // Example: "INTEGER", "BOOLEAN", "STRING" for parsing
}
