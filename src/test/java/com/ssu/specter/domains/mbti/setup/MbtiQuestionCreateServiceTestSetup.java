package com.ssu.specter.domains.mbti.setup;

import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import com.ssu.specter.domains.mbti.dto.MbtiCreateRequest;
import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.domain.embed.UserRole;

import java.io.InputStream;
import java.util.List;

import static java.util.stream.IntStream.rangeClosed;

public class MbtiQuestionCreateServiceTestSetup {

	public static MbtiQuestion.MbtiQuestionBuilder getMbtiQuestionBuilder() {
		return MbtiQuestion.builder()
				.questionId(1L)
				.question("낯선 이들과 대화하는 것이 편안하신가요?")
				.type("radio")
				;
	}

	public static User getUser() {
		return User.builder()
				.userId(1L)
				.userEmail("test@test.com")
				.userPassword("1234")
				.userPhone("010-1111-2222")
				.role(UserRole.USER)
				.build();
	}

	public static User getNoUserRole() {
		return User.builder()
				.userId(1L)
				.userEmail("noUserRole@test.com")
				.userName("김테스트")
				.userPassword("1234")
				.role(UserRole.MANAGER) // User 권한 아님
				.build();
	}

	/**
	 * 설문지 답변 성공 케이스 : 1부터 18번까지 radio / 19부터 20번까지 textarea
	 */
	public static List<MbtiQuestion> getMbtiQuestions() {
		return rangeClosed(1, 20)
				.mapToObj(i -> getMbtiQuestionBuilder()
						.questionId((long) i)
						.type(i <= 18 ? "radio" : "textarea")
						.build())
				.toList();
	}

	/**
	 * 설문지 답안 목록
	 */
	public static List<MbtiCreateRequest.MbtiAnswerInfo> getMbtiAnswers(List<MbtiQuestion> questions) {
		return questions.stream()
				.map(q -> new MbtiCreateRequest.MbtiAnswerInfo(
						q.getQuestionId(),
						q.getType().equals("radio") ? "Y" : "텍스트 응답처리"))
				.toList();
	}

	/**
	 * 설문지 답안 목록 (19개만 생성해서 1개 누락된 실패 케이스용)
	 */
	public static List<MbtiCreateRequest.MbtiAnswerInfo> getMbtiAnswersWithOneMissing(List<MbtiQuestion> questions) {
		return getMbtiAnswers(questions).subList(0, 19); // 첫 19개만 사용
	}
}
