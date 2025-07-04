package com.ssu.specter.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.global.config.security.jwt.JwtAuthTokenProvider;
import com.ssu.specter.global.config.security.jwt.JwtTokenPayload;
import com.ssu.specter.global.util.ConverterUtil;
import com.ssu.specter.test.config.TestConfig;
import com.ssu.specter.test.config.TestProfile;
import com.ssu.specter.test.setup.DatabaseCleaner;
import com.ssu.specter.test.setup.IntegrationTestSetup;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * 통합 테스트
 */
@ActiveProfiles(TestProfile.TEST)
@Getter
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@EnableConfigurationProperties
@SpringJUnitConfig(classes = {TestConfig.class})
@AutoConfigureMockMvc
@SuppressWarnings(value = "unused")
public abstract class IntegrationTest {
	@Autowired
	private IntegrationTestSetup integrationTestSetup;

	@Autowired
	@Getter
	private WireMockServer wireMockServer;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@Autowired
	private JwtAuthTokenProvider jwtProvider;

	public void setup() {
		integrationTestSetup.setup();
		if (!getWireMockServer().isRunning()) { // 테스트에 의해 서버가 중단되었다면
			getWireMockServer().start(); // 서버를 재시작한다.
		}
	}

	/**
	 * 유저 권한 토큰 설정
	 */
	public final String getMockUserJwtToken() {
		JwtTokenPayload payload = JwtTokenPayload.builder()
				.userId(1L)
				.userEmail("test@test.com")
				.userName("김테스트")
				.roles(List.of(UserRole.USER.name()))
				.build();

		return jwtProvider.createAuthToken(payload).getToken();
	}

	/**
	 * 관리자 권한 토큰 설정
	 */
	public final String getMockManagerJwtToken() {
		JwtTokenPayload payload = JwtTokenPayload.builder()
				.userId(2L)
				.userEmail("test@admin.com")
				.userName("김관리자")
				.roles(List.of(UserRole.MANAGER.name()))
				.build();

		return jwtProvider.createAuthToken(payload).getToken();
	}

	/**
	 * 존재하지 않는 유저로 JWT 토큰 생성
	 */
	public final String getMockUserJwtTokenInvalid() {
		JwtTokenPayload payload = JwtTokenPayload.builder()
				.userId(9999L)
				.userEmail("invalid@test.com")
				.userName("존재하지 않음 유저")
				.roles(List.of(UserRole.USER.name()))
				.build();

		return jwtProvider.createAuthToken(payload).getToken();
	}

	// 코드에 문제가 없지만, 컴파일러가 경고를 출력하는 경우 이를 무시하려고 사용
	@SuppressWarnings("unchecked")
	public final Map<String, Object> getResultData(ResultActions resultActions) throws Exception {
		var result = ConverterUtil.convertJsonToMap(
				resultActions.andReturn().getResponse().getContentAsString());
		if (result != null) {
			return (Map<String, Object>) result.get("data");
		}
		return new HashMap<>();
	}

	/**
	 * {
	 *   "status": "success",
	 *   "data": {
	 *     "id": 1,
	 *     "name": "Test User",
	 *     "email": "test@test.com"
	 *   }
	 * }
	 */
	public void setupWireMockBadGateway() {
		getWireMockServer().stubFor(any(anyUrl())
				.willReturn(aResponse().
						withStatus(HttpStatus.BAD_GATEWAY.value()).
						withHeader("Content-Type", "text/html").
						withBody("Bad Gateway")));
	}
}
