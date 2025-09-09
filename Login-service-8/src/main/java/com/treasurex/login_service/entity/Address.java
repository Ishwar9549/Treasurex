package com.treasurex.login_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Address Line 1 is mandatory")
    @Size(max = 255)
    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Size(max = 255)
    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @NotBlank(message = "City is mandatory")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is mandatory")
    @Size(max = 100)
    @Column(name = "state_province", nullable = false, length = 100)
    private String stateProvince;

    @Size(max = 100)
    @Column(length = 100)
    private String district;

    @NotBlank(message = "Postal Code is mandatory")
    @Size(max = 20)
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @NotBlank(message = "Address Type is mandatory")
    @Column(name = "address_type", nullable = false, length = 50)
    private String type; // RESIDENTIAL, BUSINESS, etc.

    // Reference back to User
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "userId",nullable = false, unique = true)
    private User user;

    // audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
