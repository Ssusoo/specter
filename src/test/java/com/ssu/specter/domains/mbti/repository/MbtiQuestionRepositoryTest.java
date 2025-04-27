package com.ssu.specter.domains.mbti.repository;

import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import com.ssu.specter.test.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.ssu.specter.domains.mbti.setup.MbtiQuestionDomainSetup.getMbtiQuestions;
import static com.ssu.specter.global.constant.CommonConstant.Type.RADIO;
import static com.ssu.specter.global.constant.CommonConstant.Type.TEXTAREA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MbtiQuestionRepositoryTest extends RepositoryTest {
	@Autowired
	private MbtiQuestionRepository mbtiQuestionRepository;

	@BeforeEach
	void setUp() {
		super.getDatabaseCleaner().truncateAll();
		getMbtiQuestions().forEach(question -> getEntityManager().persist(question));
	}

	@Test
	@DisplayName("성격 유형 검사 목록 - 성공")
	void find_mbti_questions_success() {
		// when
		List<MbtiQuestion> mbtiQuestions = mbtiQuestionRepository.findMbtiQuestions();

		// then
		assertEquals(20, mbtiQuestions.size());
		assertEquals("낯선 이들과 대화하는 것이 편안하신가요?", mbtiQuestions.get(0).getQuestion());
		assertEquals(RADIO, mbtiQuestions.get(0).getType());
		assertEquals("직관적 판단이 도움이 되었던 경험을 설명해주세요.", mbtiQuestions.get(19).getQuestion());
		assertEquals(TEXTAREA, mbtiQuestions.get(19).getType());
	}
}