package com.ssu.specter.domains.mbti.repository;

import com.ssu.specter.base.repository.BaseRepository;
import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import com.ssu.specter.domains.mbti.domain.QMbtiQuestion;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MbtiQuestionRepository extends BaseRepository<MbtiQuestion, Long> {
	private static final QMbtiQuestion mbtiQuestion = QMbtiQuestion.mbtiQuestion;

	public List<MbtiQuestion> findMbtiQuestions() {
		return selectFrom(mbtiQuestion).fetch();
	}
}
