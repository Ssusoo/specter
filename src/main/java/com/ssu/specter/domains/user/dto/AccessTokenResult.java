package com.ssu.specter.domains.user.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccessTokenResult(
		String accessToken,
		String refreshToken,
		LocalDateTime refreshTokenExpireAt) {
}
