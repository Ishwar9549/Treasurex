package com.treasurex.login_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

	@Pattern(regexp = "NORMAL_USER|BUSINESS_USER|ADVISOR_USER", message = "Invalid role it must be (NORMAL_USER|BUSINESS_USER|ADVISOR_USER)")
	@NotBlank(message = "User role cannot be blank")
	@Column(nullable = false)
	private String role;

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

	@Pattern(regexp = "Aadhar|Passport|Pan", message = "Invalid ID type")
	@Column(nullable = false)
	private String idType;
 
	
	private String idNumber;

	@Builder.Default
	@Column(nullable = false)
	private boolean verified = false;

	private String otp;

	private LocalDateTime otpExpiry;

	private String verifyQuestion;

	// One-to-one with Address
	@Valid
	@NotNull(message = "Address cannot be null")
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Address address;

	// One-to-many with Security Questions
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SecurityQuestion> securityQuestions;

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
