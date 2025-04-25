package com.ssu.specter.domains.mbti.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Schema(description = "MBTI 응답 생성 요청", example = """
{
  "mbtiAnswerInfoList": [
    { "questionId": 1, "answer": "Y" },
    { "questionId": 2, "answer": "Y" },
    { "questionId": 3, "answer": "N" },
    { "questionId": 4, "answer": "Y" },
    { "questionId": 5, "answer": "Y" },
    { "questionId": 6, "answer": "N" },
    { "questionId": 7, "answer": "Y" },
    { "questionId": 8, "answer": "Y" },
    { "questionId": 9, "answer": "N" },
    { "questionId": 10, "answer": "Y" },
    { "questionId": 11, "answer": "Y" },
    { "questionId": 12, "answer": "N" },
    { "questionId": 13, "answer": "Y" },
    { "questionId": 14, "answer": "Y" },
    { "questionId": 15, "answer": "Y" },
    { "questionId": 16, "answer": "Y" },
    { "questionId": 17, "answer": "Y" },
    { "questionId": 18, "answer": "N" },
    { "questionId": 19, "answer": "신뢰와 소통이라고 생각합니다." },
    { "questionId": 20, "answer": "한 번은 직감적으로 선택한 프로젝트 팀이 최고의 결과를 냈던 적이 있어요." }
  ]
}
""")
@Builder
public record MbtiCreateRequest(
		@NotEmpty @Valid List<MbtiAnswerInfo> mbtiAnswerInfoList
) {
	public record MbtiAnswerInfo(
			@NotNull Long questionId,
			@NotNull String answer
	) {}
}