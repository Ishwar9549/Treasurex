package com.treasurex.login_service.featureflag;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "feature_flags")
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;  // e.g., "OTP_RESEND_ENABLED"

    @Column(nullable = false)
    private boolean enabled; // true = ON, false = OFF

    private String description;
}
