package com.ssu.specter.domains.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 * 로그인 : 클라이언트 요청을 받을 때 사용하는 DTO
 */
@Builder
public record UserLoginRequest(
		@NotEmpty @JsonProperty("email") @Schema(description = "이메일", defaultValue = "test@test.com") String userEmail,
		@NotEmpty @JsonProperty("password") @Schema(description = "비밀번호", defaultValue = "1234") String userPassword
) {
}
