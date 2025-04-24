package com.ssu.specter.domains.user.dto;

import com.ssu.specter.domains.user.domain.embed.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class UserSignUpRequestTest {
	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	public static void beforeAll() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	public static void afterAll() {
		validatorFactory.close();
	}

	@DisplayName("파라메터 검증 - 통과")
	@Test
	void parameterValidation_pass() {
		// given
		var request = UserSignUpRequest.builder()
				.userEmail("test@test.com")
				.userPassword("1234")
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> validate = validator.validate(request);

		// then
		assertTrue(validate.isEmpty());
	}

	static Stream<Arguments> methodSource_parameterValidation_failure() {
		return Stream.of(
				arguments("", "1234", "김테스트", "010-1234-1234", UserRole.USER), // 아이디 빈값
				arguments("test@test.com", "", "김테스트", "010-1234-1234", UserRole.USER), // 비밀번호 빈값
				arguments("test@test.com", "1234", "", "010-1234-1234", UserRole.USER), // 이름 빈값
				arguments("test@test.com", "1234", "김테스트", "", UserRole.USER), // 전화번호 빈값
				arguments("test@test.com", "1234", "김테스트", "010-1234-1234", null) // 유저 권한 없음
		);
	}

	@DisplayName("파라메터 검증 - 실패")
	@ParameterizedTest
	@MethodSource("methodSource_parameterValidation_failure")
	void parameterValidation_failure(String userEmail, String userPassword, String userName, String userPhone, UserRole userRole) {
		// given
		var request = UserSignUpRequest.builder()
				.userEmail(userEmail)
				.userPassword(userPassword)
				.userName(userName)
				.userPhone(userPhone)
				.role(userRole)
				.build();

		// when
		var validate = validator.validate(request);

		// then
		assertEquals(1, validate.size());
	}
}