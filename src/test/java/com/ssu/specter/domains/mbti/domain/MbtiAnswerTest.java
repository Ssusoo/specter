package com.ssu.specter.domains.mbti.domain;

import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.test.MockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MbtiAnswerTest extends MockTest {

	@Test
	@DisplayName("성격 유형 검사 설문지 답변 - 성공")
	void mbti_answer_success() {
		User user = User.builder()
				.userId(1L)
				.userEmail("test@test.com")
				.userPassword("1234")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				.build();

		String answerJson = """
				[
					{ "questionId": 1, "answer": "Y" },
					{ "questionId": 2, "answer": "N" }
				]
				""";

		MbtiAnswer result = MbtiAnswer.create(user, answerJson);

		assertThat(result.getUser().getUserEmail()).isEqualTo("test@test.com");
		assertThat(result.getAnswerData()).isEqualTo(answerJson);
	}
}
