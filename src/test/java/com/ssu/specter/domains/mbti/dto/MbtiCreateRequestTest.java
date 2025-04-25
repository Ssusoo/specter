package com.ssu.specter.domains.mbti.dto;

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

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MbtiCreateRequestTest {
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

	@Test
	@DisplayName("파라미터 검증 - 통과")
	void parameterValidation_pass() {
		// given
		MbtiCreateRequest request = MbtiCreateRequest.builder()
				.mbtiAnswerInfoList(List.of(
						new MbtiCreateRequest.MbtiAnswerInfo(1L, "Y"),
						new MbtiCreateRequest.MbtiAnswerInfo(2L, "N")
				))
				.build();
		Set<ConstraintViolation<MbtiCreateRequest>> validate = validator.validate(request);


		// then
		assertTrue(validate.isEmpty());
	}

	static Stream<Arguments> methodSource_parameterValidation_failure() {
		return Stream.of(
				arguments(null, "Y"), // 아이디 빈값
				arguments(1L, null) // 답변 빈값
		);
	}

	@DisplayName("파라메터 검증 - 실패")
	@ParameterizedTest
	@MethodSource("methodSource_parameterValidation_failure")
	void parameterValidation_failure(Long questionId, String answer) {
		// given
		MbtiCreateRequest request = MbtiCreateRequest.builder()
				.mbtiAnswerInfoList(List.of(
						new MbtiCreateRequest.MbtiAnswerInfo(questionId, answer),
						new MbtiCreateRequest.MbtiAnswerInfo(questionId, answer)
				))
				.build();

		// when
		var validate = validator.validate(request);

		// then
		assertEquals(2, validate.size());
	}
}