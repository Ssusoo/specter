package com.ssu.specter.domains.user.controller;

import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.dto.AccessTokenRenewRequest;
import com.ssu.specter.domains.user.dto.UserLoginRequest;
import com.ssu.specter.domains.user.dto.UserSignUpRequest;
import com.ssu.specter.domains.user.setup.UserControllerTestSetup;
import com.ssu.specter.global.util.ConverterUtil;
import com.ssu.specter.test.IntegrationTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends IntegrationTest {
	@Autowired
	private UserControllerTestSetup userControllerTestSetup;

	@BeforeEach
	public void beforeEach() {
		super.setup();
		super.getDatabaseCleaner().truncateAll();
		try {
			userControllerTestSetup.setup();
		} catch (Exception ignored) {
		}
	}

	@DisplayName("회원 가입 - 성공")
	@Test
	void signUp_success() throws Exception {
		// given
		var request = getSignUpRequestBuilder().build();// 가입이 가능한 회원

		// when
		var resultActions = perform(request);

		// then
		resultActions.andExpect(status().isOk());
	}

	@DisplayName("회원 가입 - 실패")
	@ParameterizedTest
	@ValueSource(strings = {"이미 가입 된 아이디"})
	void signUp_failure(String testCase) throws Exception {
		// given
		var request = getSignUpRequestBuilder()
				.userEmail(testCase.equals("이미 가입 된 아이디") ? "alreadySignUpUser" : "test@test.com")
				.build();

		// when
		var resultActions = perform(request);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@DisplayName("로그인 - 실패 - 아이디 및 비밀번호 불일치")
	@ParameterizedTest
	@ValueSource(strings = {"아이디 불일치", "비밀번호 불일치"})
	void login_failure(String testCase) throws Exception {
		// given
		var requestBuilder = getLoginRequestBuilder();
		if (testCase.equals("아이디 불일치")) {
			requestBuilder.userEmail("not-matched-user-id");
		} else if (testCase.equals("비밀번호 불일치")) {
			requestBuilder.userPassword("not-matched-user-password");
		}

		// when
		ResultActions resultActions = perform(requestBuilder.build());

		// then
		resultActions.andExpect(status().is(401));
	}

	@DisplayName("로그인 > AccessToken 갱신 > 로그아웃 시나리오 테스트")
	@TestFactory
	Stream<DynamicTest> login_and_renewAccessToken_and_logout() {
		AtomicReference<String> accessToken = new AtomicReference<>();
		AtomicReference<String> refreshToken = new AtomicReference<>();
		return Stream.of(
				dynamicTest("로그인", () -> {
					// given
					UserLoginRequest request = getLoginRequestBuilder().build();

					// when
					ResultActions resultActions = perform(request);

					// then
					Map<String, Object> resultData = super.getResultData(resultActions);
					refreshToken.set((String) resultData.get("refreshToken"));

					resultActions.andExpect(status().isOk())
							.andExpect(jsonPath("$.data.accessToken").isString())
							.andExpect(jsonPath("$.data.refreshToken").isString())
							.andExpect(jsonPath("$.data.refreshTokenExpireAt").isString());
				}),
				dynamicTest("AccessToken 갱신", () -> {
					// given
					AccessTokenRenewRequest request = getAccessTokenRenewRequestBuilder()
							.refreshToken(refreshToken.get())
							.build();

					// when
					var resultActions = perform(request);

					// then
					Map<String, Object> resultData = super.getResultData(resultActions);
					accessToken.set((String) resultData.get("accessToken"));

					resultActions.andExpect(status().isOk());
				}),
				dynamicTest("로그아웃", () -> {
					ResultActions resultActions = perform(accessToken.get());

					// then
					resultActions.andExpect(status().isOk());
				})
		);
	}

	private ResultActions perform(UserSignUpRequest request) throws Exception {
		String body = ConverterUtil.convertObjectToJson(request);
		return getMockMvc().perform(post("/specter/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(body))
				.andDo(print());
	}

	private ResultActions perform(UserLoginRequest request) throws Exception {
		var body = ConverterUtil.convertObjectToJson(request);
		return getMockMvc().perform(post("/specter/login")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(body))
				.andDo(print());
	}

	private ResultActions perform(String accessToken) throws Exception {
		return getMockMvc().perform(post("/specter/logout")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + accessToken)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	private ResultActions perform(AccessTokenRenewRequest request) throws Exception {
		var body = ConverterUtil.convertObjectToJson(request);
		return getMockMvc().perform(post("/specter/renew-access-token")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(body))
				.andDo(print());
	}

	private UserSignUpRequest.UserSignUpRequestBuilder getSignUpRequestBuilder() {
		return UserSignUpRequest.builder()
				.userEmail("test@test.com")
				.userPassword("1234")
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				;
	}

	private UserLoginRequest.UserLoginRequestBuilder getLoginRequestBuilder() {
		return UserLoginRequest.builder()
				.userEmail("alreadySignUpUser")
				.userPassword("1234");
	}

	private AccessTokenRenewRequest.AccessTokenRenewRequestBuilder getAccessTokenRenewRequestBuilder() {
		return AccessTokenRenewRequest.builder()
				.userEmail("alreadySignUpUser");
	}
}