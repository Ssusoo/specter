package com.ssu.specter.domains.user.domain.embed;

import com.ssu.specter.domains.user.exception.RefreshTokenRenewFailureException;
import com.ssu.specter.test.MockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class CertificationTest extends MockTest {
	@DisplayName("인증 처리 - 성공")
	@Test
	void cert_success() {
		// given
		var certification = Certification.builder().build();

		// when
		certification.cert();

		// then
		assertNotNull(certification.getCertAt());
		assertNotNull(certification.getRefreshToken());
		assertNotNull(certification.getRefreshTokenExpireAt());
	}

	@DisplayName("RefreshToken 갱신 - 성공")
	@Test
	void renewRefreshToken_success() {
		// given
		var currentRefreshToken = "refresh-token";
		var currentRefreshTokenExpireAt = LocalDateTime.now().plusMinutes(10);
		var certification = Certification.builder()
				.refreshToken(currentRefreshToken)
				.refreshTokenExpireAt(currentRefreshTokenExpireAt)
				.build();

		// when
		certification.renewRefreshToken();

		// then
		assertNotEquals(currentRefreshToken, certification.getRefreshToken());
		assertNotEquals(currentRefreshTokenExpireAt, certification.getRefreshTokenExpireAt());
	}

	@DisplayName("RefreshToken 갱신 - 실패 - RefreshToken 만료 됨")
	@Test
	void renewRefreshToken_failure_expiredRefreshToken() {
		// given
		var certification = Certification.builder()
				.refreshToken("refresh-token")
				.refreshTokenExpireAt(LocalDateTime.now())
				.build();

		// when
		// then
		assertThrowsExactly(RefreshTokenRenewFailureException.class,
				certification::renewRefreshToken);
	}

	@DisplayName("RefreshToken 만료처리 - 성공")
	@Test
	void revokeRefreshToken_success() {
		// given
		var certification = Certification.builder()
				.refreshToken("refresh-token")
				.refreshTokenExpireAt(LocalDateTime.now().plusHours(1))
				.build();

		var expectedRefreshTokenExpireAt = LocalDateTime.now();
		try (var localDateTime = mockStatic(LocalDateTime.class)) {
			// 현재시간 mocking, 로그아웃이 성공한다면 해당 일시로 리프레시토큰 만료일시가 업데이트 된다.
			localDateTime.when(LocalDateTime::now).thenReturn(expectedRefreshTokenExpireAt);

			// when
			certification.revokeRefreshToken();

			// then
			assertEquals(expectedRefreshTokenExpireAt, certification.getRefreshTokenExpireAt());
		}
	}
}