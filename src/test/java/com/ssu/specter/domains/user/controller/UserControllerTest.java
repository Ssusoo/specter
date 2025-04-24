package com.ssu.specter.domains.user.controller;

import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.dto.UserSignUpRequest;
import com.ssu.specter.domains.user.setup.UserControllerTestSetup;
import com.ssu.specter.global.util.ConverterUtil;
import com.ssu.specter.test.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

	private ResultActions perform(UserSignUpRequest request) throws Exception {
		String body = ConverterUtil.convertObjectToJson(request);
		return getMockMvc().perform(post("/specter/signup")
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
}