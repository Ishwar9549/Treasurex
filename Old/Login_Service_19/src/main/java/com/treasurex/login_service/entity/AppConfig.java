package com.treasurex.login_service.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing an application configuration stored in database. Supports
 * dynamic APP-level or microservice-level configurations.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "app_configs")
public class AppConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "key_name", nullable = false, unique = true)
	private String keyName; // Example: "MAX_LOGIN_ATTEMPTS"

	@Column(nullable = false)
	private String value; // Stored as String for flexibility

	@Column(nullable = false)
	private String description; // What this config does

	@Column(nullable = false)
	private String type; // "STRING", "BOOLEAN", "INTEGER" etc.
}
//END