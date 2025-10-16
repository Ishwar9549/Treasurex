package com.treasurex.login_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "address_line1", length = 255)
	private String addressLine1;

	@Column(name = "address_line2", length = 255)
	private String addressLine2;

	@Column(length = 100)
	private String city;

	@Column(name = "state", length = 100)
	private String state;

	@Column(length = 100)
	private String district;

	@Column(name = "postal_code", length = 20)
	private String postalCode;

	@Column(name = "address_type", length = 50)
	private String addressType;

	// Reference back to User
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
	private User user;

	// audit fields
	@Column(name = "created_at", updatable = false)
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
