package com.ssu.specter.domains.user.dto.payload;

import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.dto.UserSignUpRequest;
import lombok.Builder;

/**
 * 회원가입 : Service/Entity 로직 내부에서 사용하는 변환용 DTO
 */
@Builder
public record UserCreatePayload (
	String userEmail,
	String userPassword,
	String userName,
	String userPhone,
	UserRole userRole
) {
	public UserCreatePayload(UserSignUpRequest request) {
		this(
				request.userEmail(),
				request.userPassword(),
				request.userName(),
				request.userPhone(),
				request.role()
		);
	}
}
