package com.ssu.specter.global.error;

import com.ssu.specter.global.constant.ActiveProfile;
import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.dto.ErrorResponse;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	private final Environment environment;

	/**
	 * ModelAttribute 으로 binding error 발생시 BindException 발생
	 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args">참조</a>
	 * @param ex BindException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler({
			BindException.class,
			MethodArgumentNotValidException.class
	})
	protected ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
		return setResponse(ErrorResponse.of(ApiResponseCode.INVALID_INPUT_VALUE, ex.getBindingResult()));
	}

	/**
	 * enum type 일치하지 않아 binding 못할 경우 발생 주로 @RequestParam enum 으로 binding 못했을 경우 발생
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException ex) {
		return setResponse(ErrorResponse.of(ex));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
			ConstraintViolationException ex) {
		return setResponse(ErrorResponse.of(ex));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		return setResponse(ErrorResponse.of(ApiResponseCode.BODY_READ_FAILED, ex.getMessage()));
	}

	/**
	 * Authentication 객체가 필요한 권한을 보유하지 않은 경우
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException() {
		return setResponse(ErrorResponse.of(ApiResponseCode.ACCESS_DENIED));
	}

	/**
	 * 인증관련 Exception
	 * @param ex AuthenticationException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler({ InsufficientAuthenticationException.class, BadCredentialsException.class,
			InternalAuthenticationServiceException.class, AuthenticationException.class })
	protected ResponseEntity<ErrorResponse> handleUnAuthenticationException(AuthenticationException ex) {
		var errorCode = ApiResponseCode.UNAUTHORIZED;
		if (ex instanceof InternalAuthenticationServiceException || ex instanceof BadCredentialsException) {
			errorCode = ApiResponseCode.LOGIN_FAILURE;
		}
		return setResponse(ErrorResponse.of(errorCode));
	}

	/**
	 * 지원하지 않은 HTTP method 호출 할 경우 발생
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported() {
		return setResponse(ErrorResponse.of(ApiResponseCode.METHOD_NOT_ALLOWED));
	}

	/**
	 * Http Status 415
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException() {
		return setResponse(ErrorResponse.of(ApiResponseCode.UNSUPPORTED_MEDIA_TYPE));
	}

	/**
	 * 비즈니스 Exception
	 * @param ex BusinessException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler({ BusinessRuntimeException.class })
	protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessRuntimeException ex) {
		return setResponse(ErrorResponse.of(ex.getErrorCode(), ex.getErrorMessage()));
	}

	/**
	 * Http Status 404
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	protected ResponseEntity<ErrorResponse> handleNoHandlerFoundException() {
		return setResponse(ErrorResponse.of(ApiResponseCode.NOT_FOUND));
	}

	/**
	 * Servlet Exception 처리
	 */
	@ExceptionHandler(ServletException.class)
	protected ResponseEntity<ErrorResponse> handleServletException(ServletException ex) {
		log.error("handleServletException", ex);
		return setResponse(ErrorResponse.of(ex));
	}

	/**
	 * JSON Converter 에러
	 * 주로, dto 클래스에 Getter 메서드가 누락된 경우 발생한다.
	 * szs body 에 중첩되어 결과가 생성되는 현상이 있어서 apiFilter 쪽에 exception 처리를 throwable 한다.
	 */
	@ExceptionHandler(HttpMessageConversionException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageConversionException(HttpMessageConversionException ex) {
		log.error("handleHttpMessageConversionException", ex);
		throw ex;
	}

	/**
	 * 필수 아규먼트 누락 에러
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		return setResponse(ErrorResponse.of(ApiResponseCode.BAD_REQUEST, ex.getMessage()));
	}

	/**
	 * 기타 Exception 처리
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("handleException", ex);
		return setResponse(ErrorResponse.of(ex));
	}

	private ResponseEntity<ErrorResponse> setResponse(ErrorResponse errorResponse) {
		for (String profileName : environment.getActiveProfiles()) {
			if (ActiveProfile.PROD.equals(profileName)) { // 운영 환경은 stacktrace 정보 제거
				errorResponse.removeStackTrace();
			}
		}
		return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
	}
}
