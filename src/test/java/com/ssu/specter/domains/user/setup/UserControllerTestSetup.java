package com.ssu.specter.domains.user.setup;


import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.test.config.TestProfile;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.ssu.specter.domains.user.setup.UserDomainBuilder.getUserBuilder;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
@Profile(TestProfile.TEST)
public class UserControllerTestSetup {
	private final EntityManager entityManager;
	private final PasswordEncoder passwordEncoder;

	public void setup() throws Exception {
		// 기 가입 회원 등록
		entityManager.persist(getUserBuilder()
				.userEmail("alreadySignUpUser")
				.userPassword(passwordEncoder.encode("1234"))
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				.build());
	}
}
