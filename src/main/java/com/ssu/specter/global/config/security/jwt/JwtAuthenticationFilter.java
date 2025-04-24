package com.ssu.specter.global.config.security.jwt;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.dto.ErrorResponse;
import com.ssu.specter.global.error.exception.JwtTokenNotFoundException;
import com.ssu.specter.global.error.exception.TokenValidationFailureException;
import com.ssu.specter.global.util.HttpUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;

/**
 * JWT 인증 필터
 *  이 필터는 요청이 들어올 때마다 작동해서 JWT를 꺼내고 검증하는 역할
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtAuthTokenProvider jwtAuthTokenProvider;

	@SuppressWarnings("all")
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		if (request.getRequestURI().startsWith("/actuator/") || request.getRequestURI().startsWith("/api-docs/swagger")) {
			filterChain.doFilter(request, response);
		} else {
			var requestWrapper = new ContentCachingRequestWrapper(request);
			var responseWrapper = new ContentCachingResponseWrapper(response);

			try {
				if (!isNotAuthenticatePath(request.getRequestURI())) {
					var token = HttpUtils.getBearerJwtToken(request);
					if (token != null) {
						var jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
						if (jwtAuthToken.validate()) { // 토큰이 정상이라면..
							var authentication = jwtAuthTokenProvider.getAuthentication(jwtAuthToken);
							SecurityContextHolder.getContext().setAuthentication(authentication);
						}
					}
				}
				filterChain.doFilter(requestWrapper, responseWrapper);
				responseWrapper.copyBodyToResponse();
			} catch (JwtTokenNotFoundException | MalformedJwtException | IllegalArgumentException |
			         TokenValidationFailureException ex) {
				ErrorResponse.setErrorResponse(response,
						ApiResponseCode.INVALID_TOKEN,
						ErrorResponse.of(ApiResponseCode.INVALID_TOKEN));
			} catch (SecurityException | UnsupportedJwtException ex) {
				ErrorResponse.setErrorResponse(response,
						ApiResponseCode.EXPIRED_OR_INVALID_TOKEN,
						ErrorResponse.of(ApiResponseCode.EXPIRED_OR_INVALID_TOKEN));
			} catch (ExpiredJwtException ex) {
				ErrorResponse.setErrorResponse(response,
						ApiResponseCode.EXPIRED_TOKEN,
						ErrorResponse.of(ApiResponseCode.EXPIRED_TOKEN));
			} catch (Exception ex) {
				if (!(ex instanceof ClientAbortException)) {
					log.error("doFilterInternal", ex);
					ErrorResponse.setErrorResponse(response,
							ApiResponseCode.INTERNAL_SERVER_ERROR,
							ErrorResponse.of(ex));
				}
			}
		}
	}

	private boolean isNotAuthenticatePath(String uri) {
		return Arrays.asList("/specter/signup", "/specter/login", "/specter/renew-access-token")
				.contains(uri.replaceAll("^/v\\d", ""));
	}
}

