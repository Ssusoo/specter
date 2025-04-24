package com.ssu.specter.global.config.security.jwt;

import org.springframework.security.core.Authentication;

public interface AuthTokenProvider<T> {
	/**
	 * JWT AccessToken 생성
	 */
	T createAuthToken(JwtTokenPayload tokenPayload);

	T convertAuthToken(String token);

	Authentication getAuthentication(T authToken);
}
