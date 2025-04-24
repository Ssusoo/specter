package com.ssu.specter.global.application;

import com.ssu.specter.global.constant.code.CodeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringToEnumConverterFactory implements ConverterFactory<String, Enum<? extends CodeEnum>> {

	@SuppressWarnings("NullableProblems")
	@Override
	public <T extends Enum<? extends CodeEnum>> Converter<String, T> getConverter(Class<T> targetType) {
		if (CodeEnum.class.isAssignableFrom(targetType)) {
			return new StringCodeToEnumConverter<>(targetType);
		} else {
			return null;
		}
	}

	private static final class StringCodeToEnumConverter<T extends Enum<? extends CodeEnum>> implements Converter<String, T> {
		private final Map<String, T> map;

		public StringCodeToEnumConverter(Class<T> targetEnum) {
			T[] enumConstants = targetEnum.getEnumConstants();
			map = Arrays.stream(enumConstants)
					.collect(Collectors.toMap(enumConstant -> ((CodeEnum) enumConstant).getCode(), Function.identity()));
		}

		@Override
		public T convert(String enumCode) {
			// 해당 값 존재 여부 확인
			if (!StringUtils.hasText(enumCode)) {
				return null;
			}

			// 해당 값 map 에서 추출
			T enumValue = map.get(enumCode);
			// 해당 값이 map 에 존재하지 않을 경우 Exception 처리
			if (enumValue == null) {
				throw new IllegalArgumentException("IllegalArgumentException");
			}
			return enumValue;
		}
	}
}
