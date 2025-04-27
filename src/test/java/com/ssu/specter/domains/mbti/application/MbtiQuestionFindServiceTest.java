package com.ssu.specter.domains.mbti.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssu.specter.domains.mbti.domain.MbtiAnswer;
import com.ssu.specter.domains.mbti.repository.MbtiAnswerRepository;
import com.ssu.specter.domains.mbti.repository.MbtiQuestionRepository;
import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.exception.UserRoleAccessDeniedException;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.global.config.security.jwt.JwtClaimsPayload;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;
import com.ssu.specter.global.error.exception.DataNotFoundException;
import com.ssu.specter.test.MockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static com.ssu.specter.domains.mbti.setup.MbtiQuestionFindServiceTestSetup.*;
import static com.ssu.specter.global.constant.CommonConstant.Type.RADIO;
import static com.ssu.specter.global.constant.CommonConstant.Yn.N;
import static com.ssu.specter.global.constant.CommonConstant.Yn.Y;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

class MbtiQuestionFindServiceTest extends MockTest {
	@InjectMocks
	private MbtiQuestionFindService questionFindService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private MbtiQuestionRepository mbtiQuestionRepository;
	@Mock
	private MbtiAnswerRepository mbtiAnswerRepository;

	@Test
	@DisplayName("성격 유형 검사 설문 목록 - 성공")
	void mbti_question_list_success() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");

			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));

			var mbtiQuestion = getMbtiQuestionBuilder().build();
			given(mbtiQuestionRepository.findMbtiQuestions()).willReturn(List.of(mbtiQuestion));

			// when
			var results = questionFindService.getMbtiQuestions();

			// then
			assertNotNull(results);
			assertEquals(1L, results.get(0).questionId());
			assertEquals("낯선 이들과 대화하는 것이 편안하신가요?", results.get(0).question());
			assertEquals(RADIO, results.get(0).type());
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 목록 - 실패(인증한 회원 없음)")
	void mbti_question_list_failure_user_no_authentication() {
		try (MockedStatic<JwtClaimsPayload> mockedJwt = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedJwt.when(JwtClaimsPayload::getUserEmail).thenReturn(null);

			// when & then
			assertThrows(DataNotFoundException.class,
					() -> questionFindService.getMbtiQuestions());
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 목록 - 실패(유저 정보 없음)")
	void modify_failure_user_not_found() {
		try (MockedStatic<JwtClaimsPayload> mockedJwt = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedJwt.when(JwtClaimsPayload::getUserEmail).thenReturn("noUser@test.com");
			given(userRepository.findUser("noUser@test.com")).willReturn(Optional.empty());

			// when & then
			assertThrows(BusinessRuntimeException.class,
					() -> questionFindService.getMbtiQuestions());
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 목록 - 실패(권한 없음)")
	void mbti_question_list_failure_user_role_denied() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("admin@admin.com");
			var adminUser = User.builder()
					.userId(33L)
					.userEmail("admin@admin.com")
					.userName("김관리자")
					.userPassword("1234")
					.role(UserRole.MANAGER) // USER가 아님
					.build();
			given(userRepository.findUser("admin@admin.com")).willReturn(Optional.of(adminUser));

			// when & then
			assertThrows(UserRoleAccessDeniedException.class,
					() -> questionFindService.getMbtiQuestions());
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 목록 - 실패(빈 리스트")
	void mbti_question_list_failure_empty() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@test.com");

			var user = getUser();
			given(userRepository.findUser("test@test.com")).willReturn(Optional.of(user));
			given(mbtiQuestionRepository.findMbtiQuestions()).willReturn(List.of());

			// when
			var results = questionFindService.getMbtiQuestions();

			// then
			assertNotNull(results);
			assertTrue(results.isEmpty());
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 상세 - 성공")
	void mbti_question_detail_success() throws JsonProcessingException {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@admin.com");
			var user = getUser();

			String answerJson = """
				[
					{ "questionId": 1, "answer": "Y" },
					{ "questionId": 2, "answer": "N" }
				]
				""";
			var mbtiAnswer = MbtiAnswer.builder()
					.answerId(1L)
					.user(user)
					.answerData(answerJson)
					.build();

			var manager = getManager();
			given(userRepository.findUser("test@admin.com")).willReturn(Optional.of(manager));
			given(mbtiAnswerRepository.findMbtiAnswerDetail(1L)).willReturn(Optional.of(mbtiAnswer));

			// when
			var result = questionFindService.getMbtiQuestionDetail(1L);

			// Then
			assertThat(result.answerId()).isEqualTo(1L);
			assertThat(result.answerData().get(0).questionId()).isEqualTo(1L);
			assertThat(result.answerData().get(0).answer()).isEqualTo(Y);
			assertThat(result.answerData().get(1).questionId()).isEqualTo(2L);
			assertThat(result.answerData().get(1).answer()).isEqualTo(N);
		}
	}

	@Test
	@DisplayName("성격 유형 검사 설문 상세 - 실패(권한 없음)")
	void mbti_question_detail_failure_manager_role_denied() {
		try (MockedStatic<JwtClaimsPayload> mockedStatic = mockStatic(JwtClaimsPayload.class)) {
			// given
			mockedStatic.when(JwtClaimsPayload::getUserEmail).thenReturn("test@admin.com");
			var adminUser = User.builder()
					.userId(22L)
					.userEmail("test@admin.com")
					.userName("김관리자")
					.userPassword("1234")
					.role(UserRole.USER) // Manager가 아님
					.build();
			given(userRepository.findUser("test@admin.com")).willReturn(Optional.of(adminUser));

			// when & then
			assertThrows(UserRoleAccessDeniedException.class,
					() -> questionFindService.getMbtiQuestionDetail(1L));
		}
	}
}
