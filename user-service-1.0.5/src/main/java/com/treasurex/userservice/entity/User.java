package com.treasurex.userservice.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.treasurex.userservice.dto.OtpPurpose;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@JsonIgnore // internal DB id, do not expose in API
	private Long id;

	// Type of user: NORMAL_USER | BUSINESS_USER | ADVISOR_USER
	@Column(nullable = false)
	private String typeOfUser;

	// User's phone number (10 digits, unique)
	@Column(nullable = false, length = 10, unique = true)
	private String phoneNumber;

	@Builder.Default
	@Column(nullable = false)
	private boolean phoneVerified = false; // has user verified phone?

	@JsonIgnore
	private String password; // hashed password

	@Column(unique = true)
	private String email; // email, must be unique if present

	@Builder.Default
	@Column(nullable = false)
	private boolean emailVerified = false; // email verification status

	@JsonIgnore
	private String otp; // OTP sent for verification

	@Column(length = 30)
	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private OtpPurpose otpPurpose;

	@JsonIgnore
	private LocalDateTime otpExpiry; // OTP expiry timestamp

	@Builder.Default
	@Column(nullable = false)
	@JsonIgnore
	private int otpAttempts = 0;

	@Column(unique = true)
	private String userName; // optional, unique username

	@JsonIgnore
	private String mpinHash; // hashed MPIN

	// One-to-one mapping with UserDetails
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private UserDetails userDetails;

	// One-to-one mapping with AdvisorDetails
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private AdvisorDetails advisorDetails;

	// One-to-one mapping with BusinessDetails
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private BusinessDetails businessDetails;

	// One-to-one mapping with Referral system
	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private ReferalSystem referalSystem;

	// Auditing: creation timestamp (set automatically)
	@Column(updatable = false)
	private LocalDateTime createdAt;

	// Auditing: last updated timestamp (set automatically)
	private LocalDateTime updatedAt;

	// Set timestamps when creating new record
	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	// Update timestamp when record is updated
	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
//END