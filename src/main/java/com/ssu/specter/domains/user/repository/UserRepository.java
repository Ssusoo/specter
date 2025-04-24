package com.ssu.specter.domains.user.repository;

import com.querydsl.core.BooleanBuilder;
import com.ssu.specter.base.repository.BaseRepository;
import com.ssu.specter.domains.user.domain.QUser;
import com.ssu.specter.domains.user.domain.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User, Long> {
	private static final QUser user = QUser.user;

	public Optional<User> findUser(@NotNull Long userId) {
		return Optional.ofNullable(
				selectFrom(user)
					.where(eqUserId(userId))
					.fetchFirst()
		);
	}

	public Optional<User> findUser(@NotEmpty String userEmail) {
		return Optional.ofNullable(
				selectFrom(user)
						.where(eqUserEmail(userEmail))
						.fetchFirst()
		);
	}

	/**
	 * 이메일 중복 체크
	 */
	public boolean existsByUserEmail(String userEmail) {
		return selectFrom(user).where(eqUserEmail(userEmail)).fetchFirst() != null;
	}

	private BooleanBuilder eqUserEmail(String userEmail) {
		return new BooleanBuilder(user.userEmail.eq(userEmail));
	}

	private BooleanBuilder eqUserId(Long userId) {
		return new BooleanBuilder(user.userId.eq(userId));
	}
}
