package com.ssu.specter.domains.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 회원 가입 : 클라이언트 요청을 받을 때 사용하는 DTO
 */
@Builder
public record UserSignUpRequest(
		@NotEmpty @JsonProperty("email") @Schema(description = "회원 아이디", example = "test@test.com") String userEmail,

		@NotEmpty @JsonProperty("password") @Schema(description = "비밀번호", defaultValue = "1234") String userPassword,
		@NotEmpty @JsonProperty("name") @Schema(description = "이름", defaultValue = "김테스트") String userName,
		@NotEmpty @JsonProperty("phone") @Schema(description = "전화번호", defaultValue = "010-1234-1234") String userPhone,
		@NotNull @JsonProperty("role") @Schema(description = "권한", defaultValue = "USER") UserRole role
) {
}
