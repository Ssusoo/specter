package com.ssu.specter.domains.user.domain;

import com.ssu.specter.domains.user.domain.embed.Certification;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.dto.payload.UserCreatePayload;
import com.ssu.specter.test.MockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static com.ssu.specter.domains.user.setup.UserDomainBuilder.getUserBuilder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

class UserTest extends MockTest {
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Test
	@DisplayName("회원가입 - 성공")
	void signUp_success() {
		// given
		var payload = UserCreatePayload.builder()
				.userEmail("test@test.com")
				.userPassword("1234")
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.userRole(UserRole.USER)
				.build();

		// when
		var user = User.signUp(payload, passwordEncoder);

		// then
		assertThat(user.getUserEmail()).isEqualTo(payload.userEmail());
		assertThat(user.getUserName()).isEqualTo(payload.userName());
		assertThat(user.getUserPhone()).isEqualTo(payload.userPhone());
		assertThat(user.getRole()).isEqualTo(payload.userRole());

		assertThat(passwordEncoder.matches(payload.userPassword(), user.getUserPassword())).isTrue();
	}

	@ParameterizedTest
	@DisplayName("로그인 - 성공")
	@ValueSource(booleans = {true, false}) // 인증이력 존재 여부
	void login_success(boolean hasCertification) {
		// given
		var user = getUser(hasCertification);

		// 예상되는 인증 일시
		var expectedCertAt = LocalDateTime.now();

		try (var localDateTime = mockStatic(LocalDateTime.class)) {
			// 현재시간 mocking, 인증을 성공한다면 해당 일시로 업데이트 된다.
			localDateTime.when(LocalDateTime::now).thenReturn(expectedCertAt);

			// when
			user.login();

			// then
			assertEquals(expectedCertAt, user.getCertification().getCertAt());
			assertEquals(1, user.getUserCertificationHistories().size());
		}
	}

	@DisplayName("로그아웃 - 성공")
	@ParameterizedTest
	@ValueSource(booleans = {true, false}) // 인증이력 존재 여부
	void logout_success(boolean hasCertification) {
		// given
		var user = getUser(hasCertification);

		// 예상되는 리프레시 토큰 만료 일시
		var expectedRefreshTokenExpireAt = LocalDateTime.now().plusHours(1);
		try (var localDateTime = mockStatic(LocalDateTime.class)) {
			// 현재시간 mocking, 로그아웃이 성공한다면 해당 일시로 리프레시토큰 만료일시가 업데이트 된다.
			localDateTime.when(LocalDateTime::now).thenReturn(expectedRefreshTokenExpireAt);

			// when
			user.logout();

			// then
			if (!hasCertification) { // 인증이력이 없다면
				assertNull(user.getCertification());
			} else {
				assertEquals(expectedRefreshTokenExpireAt, user.getCertification().getRefreshTokenExpireAt());
			}
		}
	}

	@DisplayName("RefreshToken 반환 - 성공 - 1) 인증이력 없음 2)인증이력 있음")
	@ParameterizedTest
	@ValueSource(booleans = {true, false}) // 인증이력 존재 여부
	void getRefreshToken_success(boolean hasCertification) {
		// given
		var user = getUser(hasCertification);

		// when
		var actual = user.getRefreshToken();

		// then
		assertEquals(hasCertification, (actual != null));
	}

	private User getUser(boolean hasCertification) {
		var userBuilder = getUserBuilder();
		if (hasCertification) { // 인증이력 포함
			userBuilder
					.certification(Certification.builder()
							.certAt(LocalDateTime.now().minusDays(1))
							.refreshToken("refresh-token")
							.refreshTokenExpireAt(LocalDateTime.now().plusDays(3))
							.build());
		}
		return userBuilder.build();
	}
}