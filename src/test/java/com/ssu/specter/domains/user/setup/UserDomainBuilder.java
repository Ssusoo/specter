package com.ssu.specter.domains.user.setup;

import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.domain.embed.UserRole;

public class UserDomainBuilder {
	public static User.UserBuilder getUserBuilder() {
		return User.builder()
				.userEmail("test@test.com")
				.userName("김테스트")
				.userPassword("encoded-password")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				;
	}
}
