package com.ssu.specter.domains.mbti.setup;

import com.ssu.specter.domains.mbti.domain.MbtiAnswer;
import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.domain.embed.UserRole;

public class MbtiAnswerDomainSetup {
	public static MbtiAnswer.MbtiAnswerBuilder getMbtiAnswerBuilder() {
		var user = User.builder()
				.userId(1L)
				.userEmail("test@test.com")
				.userName("김테스트")
				.userPassword("encoded-password")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				.build();

		String answerJson = """
				[
					{ "questionId": 1, "answer": "Y" },
					{ "questionId": 2, "answer": "N" }
				]
				""";

		return MbtiAnswer.builder()
				.user(user)
				.answerData(answerJson);
	}
}
