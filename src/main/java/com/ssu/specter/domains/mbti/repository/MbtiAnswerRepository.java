package com.ssu.specter.domains.mbti.repository;

import com.querydsl.core.BooleanBuilder;
import com.ssu.specter.base.repository.BaseRepository;
import com.ssu.specter.domains.mbti.domain.MbtiAnswer;
import com.ssu.specter.domains.mbti.domain.QMbtiAnswer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MbtiAnswerRepository extends BaseRepository<MbtiAnswer, Long> {
	private final QMbtiAnswer mbtiAnswer = QMbtiAnswer.mbtiAnswer;
	
	public boolean existsByAnswer(Long userId) {
		return selectFrom(mbtiAnswer)
				.where(eqUserId(userId))
				.fetchFirst() != null;
	}

	public Optional<MbtiAnswer> findMbtiAnswerDetail(Long userId) {
		return Optional.ofNullable(selectFrom(mbtiAnswer)
				.where(eqUserId(userId))
				.fetchFirst());
	}

	private BooleanBuilder eqUserId(Long userId) {
		return new BooleanBuilder(mbtiAnswer.user.userId.eq(userId));
	}
}
