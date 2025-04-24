package com.ssu.specter.global.config.security;

import com.ssu.specter.global.application.StringToEnumConverterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping("/**")
				.maxAge(3600);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();
		dateTimeFormatterRegistrar.setUseIsoFormat(true); // ISO 포맷 사용, 그게 아니면 각자 명시적 설정
		dateTimeFormatterRegistrar.registerFormatters(registry);
		registry.addConverterFactory(new StringToEnumConverterFactory()); // EnumCode 변환, String to Enum Converter

	}
}
