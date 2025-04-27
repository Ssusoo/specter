package com.ssu.specter.domains.mbti.repository;

import com.ssu.specter.test.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ssu.specter.domains.mbti.setup.MbtiAnswerDomainSetup.getMbtiAnswerBuilder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MbtiAnswerRepositoryTest extends RepositoryTest {
	@Autowired
	private MbtiAnswerRepository mbtiAnswerRepository;

	@BeforeEach
	void setup() {
		super.getDatabaseCleaner().truncateAll();
		getEntityManager().persist(getMbtiAnswerBuilder().build());
	}

	@Test
	@DisplayName("성격 유형 검사 답변 - 성공")
	void find_mbti_answer_detail_success() {
		// when
		var mbtiAnswerDetail = mbtiAnswerRepository.findMbtiAnswerDetail(1L);

		// then
		assertTrue(mbtiAnswerDetail.isPresent());
	}

	@Test
	@DisplayName("성격 유형 검사 답변 - 실패(유저 존재하지 않음)")
	void find_mbti_answer_detail_failure() {
		// when
		var mbtiAnswerDetail = mbtiAnswerRepository.findMbtiAnswerDetail(999L);

		// then
		assertTrue(mbtiAnswerDetail.isEmpty());  // Optional이 비어있어야 함
	}

	@Test
	@DisplayName("성격 유형 검사 답변 존재 여부 확인 - 성공")
	void exists_by_answer_success() {
		// when
		boolean exists = mbtiAnswerRepository.existsByAnswer(1L);

		// then
		assertTrue(exists);
	}

	@Test
	@DisplayName("성격 유형 검사 답변 존재 여부 확인 - 실패(존재하지 않는 경우)")
	void exists_by_answer_failure() {
		// when
		boolean exists = mbtiAnswerRepository.existsByAnswer(999L);

		// then
		assertFalse(exists);
	}
}