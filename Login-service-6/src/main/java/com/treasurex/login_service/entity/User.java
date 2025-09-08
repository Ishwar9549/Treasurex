package com.treasurex.login_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @NotBlank(message = "UserId cannot be blank")
    @Column(nullable = false, unique = true)
    private String userId;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	@Column(nullable = false, unique = true)
	private String email;
	
	@NotBlank(message = "Password cannot be blank")
	@Column(nullable = false)
	private String password;
	
	@NotBlank(message = "First name cannot be blank")
    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name cannot be blank")
    @Column(nullable = false)
    private String lastName;
    
    @NotBlank(message = "Phone number cannot be blank")
    @Column(nullable = false, length = 10)
    private String phoneNumber;
    
    @Column(length = 10)
    private String alternativePhoneNumber;
    
    @Email(message = "Invalid alternative email format")
    private String alternativeEmail;
    
    private LocalDate dob;

    @Column(length = 500)
    private String address;

    private String idType;    // e.g., Aadhar, Passport, PAN
    private String idNumber;

    @Builder.Default
    @Column(nullable = false)
    private boolean verified = false;

	@Column(length = 4)
	private String otp;

	private LocalDateTime otpExpiry;

	// auditing
	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
