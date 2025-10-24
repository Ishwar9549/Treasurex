package com.treasurex.login_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

	@NotBlank(message = "Login ID (User ID / Email / Phone) cannot be blank")
	private String loginId;

	@Pattern(regexp = "PENDING_APPROVAL|APPROVED|REJECTED", message = "Invalid Approval type it must be(PENDING_APPROVAL | APPROVED | REJECTED)")
	@NotBlank(message = "Assign Status cannot be blank")
	private String assignStatus;

}
//END