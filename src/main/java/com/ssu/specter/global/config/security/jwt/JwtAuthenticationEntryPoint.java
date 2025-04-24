package com.ssu.specter.global.config.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * JWT 관련 예외 처리 핸들러
 *  토큰이 없거나 만료됐을 때 → "401 Unauthorized" 상황.
 *      내부에서는 HandlerExceptionResolver를 사용해서 예외를 전역 예외 처리기로 위임
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
	                     AuthenticationException authException) {
		handlerExceptionResolver.resolveException(request, response, null, authException);
	}
}
