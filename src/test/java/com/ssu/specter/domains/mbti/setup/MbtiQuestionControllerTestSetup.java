package com.ssu.specter.domains.mbti.setup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssu.specter.domains.mbti.domain.MbtiAnswer;
import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import com.ssu.specter.domains.mbti.dto.MbtiCreateRequest;
import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.global.util.ConverterUtil;
import com.ssu.specter.test.config.TestProfile;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Profile(TestProfile.TEST)
@RequiredArgsConstructor
@Component
public class MbtiQuestionControllerTestSetup {
	private final EntityManager entityManager;

	/**
	 * 성격 유형 검사 질문 목록
	 *  권한 : USER
	 *  설문지 답변 등록 : 등록한 경우
	 */
	@Transactional
	public void setupUserWithAnswer() throws JsonProcessingException {
		createManagerUser(); // 어드민 권한으로 사용자의 답변 상세 보기
		createUserWithAnswer(); // 유저 권한으로 답변 등록
	}

	/**
	 * 성격 유형 검사 질문 목록
	 *  권한 : USER
	 *  설문지 답변 등록 : 미등록한 경우
	 */
	@Transactional
	public void setupUserWithoutAnswer() {
		createManagerUser();
		createUserWithoutAnswer();
	}

	@Transactional
	public void setupUserWithWrongRole() {
		var user = User.builder()
				.userEmail("test@test.com")
				.userPassword("1234")
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.role(UserRole.MANAGER)
				.build();
		entityManager.persist(user);
	}

	/**
	 * 성격 유형 검사 설문지 목록 세팅
	 */
	@Transactional
	public void setupQuestion() {
		String[][] questions = {
				{"낯선 이들과 대화하는 것이 편안하신가요?", "radio"},
				{"체계적으로 일정을 관리하는 편인가요?", "radio"},
				{"타인의 마음을 읽는 것이 능숙하신가요?", "radio"},
				{"복잡한 과제를 해결하는 것을 선호하시나요?", "radio"},
				{"독립적인 활동을 즐기시나요?", "radio"},
				{"어려운 상황에서도 침착함을 유지하시나요?", "radio"},
				{"창의적인 발상이 자주 떠오르나요?", "radio"},
				{"팀 활동에 적극적으로 참여하시나요?", "radio"},
				{"상황에 민감하게 반응하시나요?", "radio"},
				{"이성적 판단을 중시하시나요?", "radio"},
				{"꼼꼼하게 일처리를 하시나요?", "radio"},
				{"봉사활동에 참여하는 것을 좋아하시나요?", "radio"},
				{"모험을 시도하는 것이 즐거우신가요?", "radio"},
				{"솔직하게 마음을 드러내시나요?", "radio"},
				{"다양한 관점을 수용하시나요?", "radio"},
				{"원하는 바를 이루기 위해 끈기 있게 도전하시나요?", "radio"},
				{"평화로운 관계 유지를 중요하게 생각하시나요?", "radio"},
				{"감정 조절이 가능한 편이신가요?", "radio"},
				{"자신의 인간관계에서 가장 중요하게 생각하는 가치는 무엇인가요?", "textarea"},
				{"직관적 판단이 도움이 되었던 경험을 설명해주세요.", "textarea"}
		};

		for (String[] q : questions) {
			entityManager.persist(
					MbtiQuestion.builder()
							.question(q[0])
							.type(q[1])
							.build()
			);
		}
	}

	private void createManagerUser() {
		var managerUser = User.builder()
				.userEmail("test@admin.com")
				.userPassword("1234")
				.userName("김관리자")
				.userPhone("010-2222-2222")
				.role(UserRole.MANAGER)
				.build();
		entityManager.persist(managerUser);
	}

	private void createUserWithAnswer() throws JsonProcessingException {
		var user = createUser();
		var answers = createAnswers();
		entityManager.persist(MbtiAnswer.builder()
				.user(user)
				.answerData(ConverterUtil.convertObjectToJson(answers))
				.build());
	}

	private void createUserWithoutAnswer() {
		createUser();
	}

	private User createUser() {
		var user = User.builder()
				.userEmail("test@test.com")
				.userPassword("1234")
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				.build();
		entityManager.persist(user);
		return user;
	}

	private List<MbtiCreateRequest.MbtiAnswerInfo> createAnswers() {
		return IntStream.rangeClosed(1, 20)
				.mapToObj(i -> new MbtiCreateRequest.MbtiAnswerInfo(
						(long) i,
						i <= 18 ? "Y" : "긴 텍스트 답변"
				))
				.toList();
	}
}
