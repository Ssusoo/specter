package com.ssu.specter.domains.mbti.setup;

import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.global.constant.CommonConstant;

import static com.ssu.specter.global.constant.CommonConstant.Type.RADIO;

public class MbtiQuestionFindServiceTestSetup {

	public static MbtiQuestion.MbtiQuestionBuilder getMbtiQuestionBuilder() {
		return MbtiQuestion.builder()
				.questionId(1L)
				.question("낯선 이들과 대화하는 것이 편안하신가요?")
				.type(RADIO)
				;
	}

	public static User getUser() {
		return User.builder()
				.userId(1L)
				.userEmail("test@test.com")
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				.build();
	}

	public static User getManager() {
		return User.builder()
				.userId(2L)
				.userEmail("test@admin.com")
				.userName("김관리자")
				.userPhone("010-2222-2222")
				.role(UserRole.MANAGER)
				.build();
	}
}
