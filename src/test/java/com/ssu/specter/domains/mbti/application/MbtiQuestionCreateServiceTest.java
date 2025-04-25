package com.ssu.specter.domains.mbti.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssu.specter.domains.mbti.dto.MbtiCreateRequest;
import com.ssu.specter.domains.mbti.exception.MbtiAnswerCreateFailureException;
import com.ssu.specter.domains.mbti.repository.MbtiAnswerRepository;
import com.ssu.specter.domains.mbti.repository.MbtiQuestionRepository;
import com.ssu.specter.domains.user.exception.UserRoleAccessDeniedException;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.global.config.security.jwt.JwtClaimsPayload;
import com.ssu.specter.test.MockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ssu.specter.domains.mbti.setup.MbtiQuestionCreateServiceTestSetup.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class MbtiQuestionCreateServiceTest extends MockTest {
	@InjectMocks
	private MbtiQuestionCreateService mbtiQuestionCreateService;
	@Mock
	private MbtiAnswerRepository mbtiAnswerRepository;
	@Mock
	private MbtiQuestionRepository mbtiQuestionRepository;
	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("성격 유형 검사 설문 등록 - 성공")
	void create_success() throws JsonProcessingException {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");
			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));
			given(mbtiAnswerRepository.existsByAnswer(user.getUserId())).willReturn(false);

			// 설문지 목록
			var mbtiQuestion = getMbtiQuestions();
			given(mbtiQuestionRepository.findMbtiQuestions()).willReturn(mbtiQuestion);

			// 설문지 답변 등록
			List<MbtiCreateRequest.MbtiAnswerInfo> answers = getMbtiAnswers(mbtiQuestion);
			var request = MbtiCreateRequest.builder()
					.mbtiAnswerInfoList(answers)
					.build();

			// when & then
			mbtiQuestionCreateService.create(request);
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 등록 - 실패(권한 없음)")
	void create_failure_user_role_denied() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");
			var noUserRole = getNoUserRole();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(noUserRole));

			// 성격 유형 검사 설문 목록
			var mbtiQuestion = getMbtiQuestions();

			// 성격 유형 검사 설문 답변 등록
			List<MbtiCreateRequest.MbtiAnswerInfo> answers = getMbtiAnswers(mbtiQuestion);
			var request = MbtiCreateRequest.builder()
					.mbtiAnswerInfoList(answers)
					.build();

			// when & then
			assertThrows(UserRoleAccessDeniedException.class,
					() -> mbtiQuestionCreateService.create(request));
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 등록 - 실패(설문지가 null일 경우")
	void create_failure_answers_is_null() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");
			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));
			given(mbtiAnswerRepository.existsByAnswer(user.getUserId())).willReturn(false);

			// 성격 유형 검사 설문 답변 등록
			var request = MbtiCreateRequest.builder()
					.mbtiAnswerInfoList(null)
					.build();

			// when & then
			assertThrows(MbtiAnswerCreateFailureException.class,
					() -> mbtiQuestionCreateService.create(request));
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 등록 - 실패(20개 중 1개 답변 부족)")
	void create_failure_answers_with_one_missing_count() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");
			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));
			given(mbtiAnswerRepository.existsByAnswer(user.getUserId())).willReturn(false);

			// 성격 유형 검사 설문 목록
			var mbtiQuestions = getMbtiQuestions();

			// 20개 중 1개 답변 부족
			var mbtiAnswersWithOneMissing = getMbtiAnswersWithOneMissing(mbtiQuestions);
			var request = MbtiCreateRequest.builder()
					.mbtiAnswerInfoList(mbtiAnswersWithOneMissing)
					.build();

			// when & then
			assertThrows(MbtiAnswerCreateFailureException.class,
					() -> mbtiQuestionCreateService.create(request));
			assertThat(mbtiAnswersWithOneMissing).hasSize(19); // 20개 중 19개 확인
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 등록 - 실패(radio에 Y/N 이외 값 입력)")
	void create_failure_invalid_type_radio_value() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");
			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));
			given(mbtiAnswerRepository.existsByAnswer(user.getUserId())).willReturn(false);

			// 성격 유형 검사 설문 목록
			var mbtiQuestions = getMbtiQuestions();
			given(mbtiQuestionRepository.findMbtiQuestions()).willReturn(mbtiQuestions);

			// radio의 다른 값 입력
			var answers = new ArrayList<>(getMbtiAnswers(mbtiQuestions));       // Java 16 이상에서는 Stream.toList()는 수정 불가능한 리스트를 반환
																				// UnsupportedOperationException이기에 List로 감쌈
																				// 수정 가능한 리스트로 변환
			answers.set(0, new MbtiCreateRequest.MbtiAnswerInfo(1L, "텍스트로 입력"));

			// 성격 유형 검사 설문 답변 등록
			var request = MbtiCreateRequest.builder()
					.mbtiAnswerInfoList(answers)
					.build();

			// when & then
			assertThrows(MbtiAnswerCreateFailureException.class,
					() -> mbtiQuestionCreateService.create(request));
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 등록 - 실패(textarea에 null 입력")
	void create_failure_type_textarea_input_is_null() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");
			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));
			given(mbtiAnswerRepository.existsByAnswer(user.getUserId())).willReturn(false);

			// 성격 유형 검사 설문 목록
			var mbtiQuestions = getMbtiQuestions();
			given(mbtiQuestionRepository.findMbtiQuestions()).willReturn(mbtiQuestions);

			// 성격 유형 검사 답변 등록
			var answers = new ArrayList<>(getMbtiAnswers(mbtiQuestions));       // Java 16 이상에서는 Stream.toList()는 수정 불가능한 리스트를 반환
																				// UnsupportedOperationException이기에 List로 감쌈
																				// 수정 가능한 리스트로 변환
			answers.set(19, new MbtiCreateRequest.MbtiAnswerInfo(20L, null));
			var request = MbtiCreateRequest.builder().mbtiAnswerInfoList(answers).build();

			// when & then
			assertThrows(MbtiAnswerCreateFailureException.class,
					() -> mbtiQuestionCreateService.create(request));
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 등록 - 실패(textarea가 공백일 때")
	void create_failure_type_textarea_input_is_empty() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");
			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));
			given(mbtiAnswerRepository.existsByAnswer(user.getUserId())).willReturn(false);

			// 성격 유형 검사 설문 목록
			var mbtiQuestions = getMbtiQuestions();
			given(mbtiQuestionRepository.findMbtiQuestions()).willReturn(mbtiQuestions);

			// 성격 유형 검사 답변 등록
			var answers = new ArrayList<>(getMbtiAnswers(mbtiQuestions));       // Java 16 이상에서는 Stream.toList()는 수정 불가능한 리스트를 반환
																				// UnsupportedOperationException이기에 List로 감쌈
																				// 수정 가능한 리스트로 변환
			answers.set(19, new MbtiCreateRequest.MbtiAnswerInfo(20L, ""));
			var request = MbtiCreateRequest.builder()
					.mbtiAnswerInfoList(answers)
					.build();

			// when & then
			assertThrows(MbtiAnswerCreateFailureException.class,
					() -> mbtiQuestionCreateService.create(request));
		}
	}
}
