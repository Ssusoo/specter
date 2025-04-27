package com.ssu.specter.domains.user.repository;

import com.ssu.specter.test.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ssu.specter.domains.user.setup.UserDomainBuilder.getUserBuilder;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest extends RepositoryTest {
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void beforeEach() {
		super.getDatabaseCleaner().truncateAll();
		super.getEntityManager().persist(getUserBuilder().build());
	}

	@Test
	@DisplayName("유저 조회(사용자 번호) - 성공")
	void find_user_id_success() {
		// when
		var user = userRepository.findUser(1L);

		// then
		assertTrue(user.isPresent());
	}

	@Test
	@DisplayName("유저 조회(사용자 번호) - 실패")
	void find_user_id_fail() {
		// when
		var user = userRepository.findUser(9999L);

		// then
		assertTrue(user.isEmpty());
	}

	@Test
	@DisplayName("유저 조회(사용자 이메일) - 성공")
	void find_user_email_success() {
		// when
		var user = userRepository.findUser("test@test.com");

		// then
		assertTrue(user.isPresent());
	}

	@Test
	@DisplayName("유저 조회(사용자 이메일) - 실패")
	void find_user_email_fail() {
		// when
		var user = userRepository.findUser("notexist@test.com");

		// then
		assertTrue(user.isEmpty());
	}
}