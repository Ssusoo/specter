package com.ssu.specter.domains.mbti.controller;

import com.ssu.specter.domains.mbti.dto.MbtiCreateRequest;
import com.ssu.specter.domains.mbti.setup.MbtiQuestionControllerTestSetup;
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

import java.util.stream.IntStream;

import static com.ssu.specter.global.constant.CommonConstant.Type.RADIO;
import static com.ssu.specter.global.constant.CommonConstant.Type.TEXTAREA;
import static com.ssu.specter.global.constant.CommonConstant.Yn.Y;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MbtiQuestionControllerTest extends IntegrationTest {
	@Autowired
	private MbtiQuestionControllerTestSetup mbtiQuestionControllerTestSetup;

	@BeforeEach
	public void beforeEach() {
		super.setup();
		super.getDatabaseCleaner().truncateAll();
		mbtiQuestionControllerTestSetup.setupQuestion(); // 성격 유형 검사 질문
	}

	@Test
	@DisplayName("성격 유형 검사 질문 목록 - 성공")
	void mbti_questions_success() throws Exception {
		// given
		mbtiQuestionControllerTestSetup.setupUserWithoutAnswer(); // 답변 등록한 경우

		// when
		var result = perform();

		// then
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data.length()").value(20)) // 20개 등록
				.andExpect(jsonPath("$.data[0].question").value("낯선 이들과 대화하는 것이 편안하신가요?"))
				.andExpect(jsonPath("$.data[0].type").value(RADIO))
				.andExpect(jsonPath("$.data[18].question").value("자신의 인간관계에서 가장 중요하게 생각하는 가치는 무엇인가요?"))
				.andExpect(jsonPath("$.data[18].type").value(TEXTAREA))
				.andExpect(jsonPath("$.data[19].question").value("직관적 판단이 도움이 되었던 경험을 설명해주세요."))
				.andExpect(jsonPath("$.data[19].type").value(TEXTAREA))
		;
	}

	@Test
	@DisplayName("성격 유형 검사 질문 목록 - 실패(유저 권한 없음)")
	void mbti_questions_failure_user_role_denied() throws Exception {
		// given
		mbtiQuestionControllerTestSetup.setupUserWithoutAnswer(); // 답변 미등록

		// when
		var result = getMockMvc().perform(get("/mbtis/questions")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", String.format("Bearer %s", getMockManagerJwtToken())) // Manager 권한
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print());

		// then
		result.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("성격 유형 검사 질문 상세 - 성공")
	void mbti_questions_detail_success() throws Exception {
		// given
		mbtiQuestionControllerTestSetup.setupUserWithAnswer(); // 답변 등록한 경우
		var userId = 2L;

		// when
		var result = perform(userId);

		// then
		result.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(2L))
				.andExpect(jsonPath("$.data.email").exists()) // tes*@test.com
				.andExpect(jsonPath("$.data.name").exists()) // 김**트
				.andExpect(jsonPath("$.data.phone").exists()) // 010-****-1234
				.andExpect(jsonPath("$.data.answerId").value(1))
				.andExpect(jsonPath("$.data.answerData").isArray())
				.andExpect(jsonPath("$.data.answerData.length()").value(20))
				.andExpect(jsonPath("$.data.answerData[0].questionId").value(1))
				.andExpect(jsonPath("$.data.answerData[0].answer").value(Y))
				.andExpect(jsonPath("$.data.answerData[19].questionId").value(20))
				.andExpect(jsonPath("$.data.answerData[19].answer").value("긴 텍스트 답변"))
		;
	}

	@DisplayName("성격 유형 검사 질문 상세 - 실패 (유저 없음, 권한 없음, 답변 없음)")
	@ParameterizedTest
	@ValueSource(strings = {"유저 없음", "권한 없음", "답변 없음"})
	void mbti_questions_detail_failure(String testCase) throws Exception {
		// given
		var userId = 999L; // 유저 없음

		if (testCase.equals("권한 없음")) {
			// USER 권한으로 토큰 설정
			mbtiQuestionControllerTestSetup.setupUserWithoutAnswer(); // 답변 있는 경우
			userId = 2L;
		} else if (testCase.equals("답변 없음")) {
			// MANAGER 권한으로 토큰 설정 & 답변 없는 유저 생성
			mbtiQuestionControllerTestSetup.setupUserWithoutAnswer(); // 답변 없는 경우
			userId = 2L;
		}

		// when
		ResultActions result = getMockMvc().perform(get("/mbtis/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", String.format("Bearer %s",
								testCase.equals("권한 없음") ? getMockUserJwtToken() : getMockManagerJwtToken()))
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print());

		// then
		if (testCase.equals("권한 없음")) {
			result.andExpect(status().isForbidden());
		} else {
			result.andExpect(status().isNotFound());
		}
	}

	@Test
	@DisplayName("성격 유형 검사 질문 등록 - 성공")
	void create_success() throws Exception {
		// given
		mbtiQuestionControllerTestSetup.setupUserWithoutAnswer(); // 답변 미등록한 경우

		MbtiCreateRequest request = createMbtiCreateRequest();
		ResultActions result = perform(request);
		result.andExpect(status().isOk())
		;
	}

	@DisplayName("성격 유형 검사 답변 등록 - 실패 케이스")
	@ParameterizedTest
	@ValueSource(strings = {"유저 없음", "권한 없음", "이미 답변 존재", "답변 수 부족"})
	void create_failure(String testCase) throws Exception {
		// given
		MbtiCreateRequest request = createMbtiCreateRequest();

		if (testCase.equals("권한 없음")) {
			mbtiQuestionControllerTestSetup.setupUserWithWrongRole();
		} else if (testCase.equals("이미 답변 존재")) {
			mbtiQuestionControllerTestSetup.setupUserWithAnswer();
		} else if (testCase.equals("답변 수 부족")) {
			mbtiQuestionControllerTestSetup.setupUserWithoutAnswer();
			request = MbtiCreateRequest.builder()
					.mbtiAnswerInfoList(
							IntStream.rangeClosed(1, 10) // 10개만 등록
									.mapToObj(i -> new MbtiCreateRequest.MbtiAnswerInfo((long) i, Y))
									.toList()
					)
					.build();
		}

		// when
		ResultActions resultActions = getMockMvc().perform(post("/mbtis/complete")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", String.format("Bearer %s",
								testCase.equals("권한 없음") || testCase.equals("답변 수 부족") || testCase.equals("이미 답변 존재")
										? getMockUserJwtToken()
										: getMockUserJwtTokenInvalid())) // 유저가 없을 경우 잘못된 토큰 사용
						.content(ConverterUtil.convertObjectToJson(request))
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print());

		// then
		if (testCase.equals("권한 없음")) {
			resultActions.andExpect(status().isForbidden());
		} else if (testCase.equals("유저 없음")) {
			resultActions.andExpect(status().isNotFound());
		} else {
			resultActions.andExpect(status().isBadRequest());
		}
	}

	private ResultActions perform() throws Exception {
		return getMockMvc().perform(get("/mbtis/questions")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", String.format("Bearer %s", getMockUserJwtToken()))
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	private ResultActions perform(Long userId) throws Exception {
		return getMockMvc().perform(get("/mbtis/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", String.format("Bearer %s", getMockManagerJwtToken()))
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	private ResultActions perform(MbtiCreateRequest request) throws Exception {
		String body = ConverterUtil.convertObjectToJson(request);
		return getMockMvc().perform(post("/mbtis/complete")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", String.format("Bearer %s", getMockUserJwtToken()))
						.accept(MediaType.APPLICATION_JSON)
						.content(body))
				.andDo(print());
	}

	private MbtiCreateRequest createMbtiCreateRequest() {
		return MbtiCreateRequest.builder()
				.mbtiAnswerInfoList(
						IntStream.rangeClosed(1, 20)
								.mapToObj(i -> new MbtiCreateRequest.MbtiAnswerInfo(
										(long) i,
										(i <= 18) ? "Y" : "신뢰와 소통이라고 생각합니다."
								))
								.toList()
				).build();
	}
}