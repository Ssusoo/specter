package com.ssu.specter.domains.user.application;

import com.ssu.specter.domains.user.domain.embed.Certification;
import com.ssu.specter.domains.user.dto.AccessTokenRenewRequest;
import com.ssu.specter.domains.user.exception.RefreshTokenRenewFailureException;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.global.config.security.jwt.JwtAuthToken;
import com.ssu.specter.global.config.security.jwt.JwtAuthTokenProvider;
import com.ssu.specter.test.MockTest;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.ssu.specter.domains.user.setup.UserDomainBuilder.getUserBuilder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class UserAuthServiceTest extends MockTest {
	@InjectMocks
	private UserAuthService userAuthService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private JwtAuthTokenProvider jwtAuthTokenProvider;

	@DisplayName("AccessToken 갱신 - 성공")
	@Test
	void renewAccessToken_success() {
		// given
		var certification = Certification.builder()
				.refreshToken("valid-refresh-token")
				.refreshTokenExpireAt(LocalDateTime.now().plusDays(1))
				.build();

		var user = getUserBuilder()
				.certification(certification)
				.build();

		given(userRepository.findUser(anyString())).willReturn(Optional.of(user));
		given(jwtAuthTokenProvider.createAuthToken(any()))
				.willReturn(new JwtAuthToken("mock-token", Keys.hmacShaKeyFor("mock-key-must-be-at-least-32bytes!".getBytes())));

		var request = AccessTokenRenewRequest.builder()
				.userEmail("test@test.com")
				.refreshToken("valid-refresh-token")
				.build();

		// when
		var result = userAuthService.renewAccessToken(request);

		// then
		assertNotNull(result);
		assertNotNull(result.accessToken());
		assertNotNull(result.refreshToken());
	}

	@DisplayName("AccessToken 갱신 - 실패")
	@ParameterizedTest
	@ValueSource(strings = {"존재하지 않는 아이디", "리프레시 토큰 불일치"})
	void renewAccessToken_failure(String testCase) {
		// given
		var request = AccessTokenRenewRequest.builder()
				.userEmail("test@test.com")
				.refreshToken("refresh-token")
				.build();
		if (testCase.equals("존재하지 않는 아이디")) {
			given(userRepository.findUser(anyString())).willReturn(Optional.empty());
		} else if (testCase.equals("리프레시 토큰 불일치")) {
			given(userRepository.findUser(anyString())).willReturn(Optional.of(
					getUserBuilder()
							.certification(Certification.builder()
									.refreshToken("refresh-token-not-matched")
									.refreshTokenExpireAt(LocalDateTime.now().minusSeconds(1))
									.build())
							.build()
			));
		}

		// when & then
		assertThrowsExactly(RefreshTokenRenewFailureException.class,
				() -> userAuthService.renewAccessToken(request));
	}
}