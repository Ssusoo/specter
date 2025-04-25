package com.ssu.specter.global.config.security.jwt;

import com.ssu.specter.global.error.exception.JwtTokenNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtClaimsPayload {
	/**
	 * Spring Security ContextHolder 에 저장된 JWT Token 을 JwtTokenPayload 객체로 변환하여 반환
	 */
	private static Optional<JwtTokenPayload> getTokenPayload() {
		try {
			var jwtAuthToken = (JwtAuthToken) SecurityContextHolder.getContext().getAuthentication().getCredentials();
			return Optional.ofNullable(
					JwtTokenPayload.from(jwtAuthToken.getData()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public static Long getUserId() {
		return getTokenPayload().map(JwtTokenPayload::userId)
				.orElseThrow(JwtTokenNotFoundException::new);
	}

	public static String getUserEmail() {
		return getTokenPayload().map(JwtTokenPayload::userEmail)
				.orElseThrow(JwtTokenNotFoundException::new);
	}

	public static boolean isPrivateInfoUser() {
		return false;
	}
}
