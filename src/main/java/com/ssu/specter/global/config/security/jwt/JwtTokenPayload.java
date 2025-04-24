package com.ssu.specter.global.config.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
public record JwtTokenPayload(Long userId, String userEmail, String userName, List<String> roles) {

	public static JwtTokenPayload from(Claims claims) {
		return JwtTokenPayload.builder()
				.userId(((Number) claims.get(JwtClaimsKey.USER_ID.getKey())).longValue())
				.userEmail(claims.get(JwtClaimsKey.USER_EMAIL.getKey()).toString())
				.userName(claims.get(JwtClaimsKey.USER_NAME.getKey()).toString())
				.roles(convertObjectToList(claims.get(JwtClaimsKey.ROLES.getKey())))
				.build();
	}

	private static List<String> convertObjectToList(Object obj) {
		List<String> list = new ArrayList<>();
		if (obj.getClass().isArray()) {
			list = Arrays.asList((String[])obj);
		}
		return list;
	}
}
