package com.ssu.specter.global.config.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtClaimsKey {
	USER_ID("userId"),
	USER_EMAIL("userEmail"),
	USER_NAME("userName"),
	ROLES("roles"),
	;

	private final String key;
}
