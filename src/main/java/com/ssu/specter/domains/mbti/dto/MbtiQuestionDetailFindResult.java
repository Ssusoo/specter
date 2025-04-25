package com.ssu.specter.domains.mbti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ssu.specter.global.util.masking.serializer.MaskingSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record MbtiQuestionDetailFindResult (
		@Schema(description = "성격 유형 검사 설문지 답변 번호") Long answerId,
		@Schema(description = "성격 유형 검사 설문지 답변") List<MbtiQuestionDetailFindResult.MbtiAnswerInfo> answerData,
		@Schema(description = "사용자 아이디") @JsonProperty("id") Long userId,
		@Schema(description = "이메일") @JsonProperty("email") @JsonSerialize(using = MaskingSerializer.Email.class) String userEmail,
		@Schema(description = "이름") @JsonProperty("name") @JsonSerialize(using = MaskingSerializer.Name.class) String userName,
		@Schema(description = "전화번호") @JsonProperty("phone") @JsonSerialize(using = MaskingSerializer.Telephone.class) String userPhone
) {
	public record MbtiAnswerInfo(
			Long questionId,
			String answer
	) {}
}
