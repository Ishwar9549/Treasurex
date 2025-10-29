package com.treasurex.login_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "business_users")
public class BusinessUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore // avoid accidental exposure in API responses.
	private Long id;

	private String businessName;

	private String businessPhone;

	private String businessPlace;

	private String panNumber;

	private String gstNumber;

	private String nomineeName;

	private String nomineeContactNumber;

	private String bankName;

	private String accountNumber;

	private String ifscCode;

	private String bio;

	// Reference back to User
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
	private User user;
}
//END