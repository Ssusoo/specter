package com.ssu.specter.domains.user.domain.embed;

import com.ssu.specter.domains.user.exception.RefreshTokenRenewFailureException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ssu.specter.global.constant.CommonConstant.Jwt;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Certification {
	@Column(name = "refresh_token", length = 36)
	private String refreshToken; // 리프레시 토큰

	@Column(name = "refresh_token_expire_dtm")
	private LocalDateTime refreshTokenExpireAt; // 리프레시 토큰 만료 일시

	@Column(name = "cert_dtm")
	private LocalDateTime certAt; // 인증 일시

	/**
	 * 인증 처리
	 */
	public void cert() {
		certAt = LocalDateTime.now();
		createRefreshToken();
	}

	/**
	 * RefreshToken 갱신 (만료되지 않은 경우만)
	 */
	public void renewRefreshToken() {
		// 리프레시 토큰 만료 여부 체크
		if (refreshTokenExpireAt.isBefore(LocalDateTime.now())) {
			throw new RefreshTokenRenewFailureException();
		}
		createRefreshToken();
	}

	/**
	 * RefreshToken 만료처리 (로그아웃)
	 */
	public void revokeRefreshToken() {
		refreshTokenExpireAt = LocalDateTime.now(); // 현재 시간으로 만료일시 저장
	}

	private void createRefreshToken() {
		refreshToken = UUID.randomUUID().toString();
		refreshTokenExpireAt = LocalDateTime.now().plusHours(Jwt.REFRESH_TOKEN_EXPIRE_HOURS);
	}
}
