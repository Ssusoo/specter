package com.ssu.specter.domains.mbti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import io.swagger.v3.oas.annotations.media.Schema;

public record MbtiQuestionFindResult(
		@Schema(description = "성격 유형 검사 설문 번호") @JsonProperty("id") Long questionId,
		@Schema(description = "성격 유형 검사 설문지") String question,
		@Schema(description = "타입") String type
) {
	public MbtiQuestionFindResult(MbtiQuestion mbtiQuestion) {
		this(
				mbtiQuestion.getQuestionId(),
				mbtiQuestion.getQuestion(),
				mbtiQuestion.getType()
		);
	}
}
