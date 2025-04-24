package com.ssu.specter.domains.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 * AccessToken 갱신 요청 : 클라이언트 요청을 받을 때 사용하는 DTO
 */
@Builder
public record AccessTokenRenewRequest(
		@NotEmpty
		@Schema(description = "사용자 아이디", defaultValue = "test@test.com")
		String userEmail,

		@NotEmpty
		@Schema(description = "Refresh Token", defaultValue = "UUID")
		String refreshToken
) {
}
