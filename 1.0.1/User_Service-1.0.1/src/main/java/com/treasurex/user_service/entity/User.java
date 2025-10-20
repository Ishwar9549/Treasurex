package com.treasurex.user_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
	@JsonIgnore // avoid accidental exposure in API responses.
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, length = 10, unique = true)
	private String phoneNumber;

	@Column(nullable = false, unique = true)
	private String userId;

	@Column(nullable = false)
	@JsonIgnore // avoid accidental exposure in API responses.
	private String password;

	// Type of user NORMAL_USER | BUSINESS_USER | ADVISOR_USER
	@Column(nullable = false)
	private String typeOfUser;

	private String firstName;

	private String middleName;

	private String lastName;

	@Column(length = 10)
	private String alternativePhoneNumber;

	private String alternativeEmail;

	private LocalDate dob;

	// GOVT Id type ADHAR | Passport | Pan
	private String govtIdType;

	private String govtIdNumber;

	// One-to-one with Address
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Address address;

	@Builder.Default
	@Column(nullable = false)
	private boolean verified = false;

	@JsonIgnore // avoid accidental exposure in API responses.
	private String otp;

	@JsonIgnore // avoid accidental exposure in API responses.
	private LocalDateTime otpExpiry;

	private String verifyQuestion;

	// One-to-many with Security Questions
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<SecurityQuestion> securityQuestions;

	// If normal User status must not go beyond VERIFIED
	// PENDING_VERIFICATION | VERIFIED | PENDING_APPROVAL | APPROVED | REJECTED
	String approvalStatus;

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
//END
