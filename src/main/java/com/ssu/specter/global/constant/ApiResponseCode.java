package com.ssu.specter.global.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiResponseCode {
	OK(200, "R20000", "정상"),
	BAD_REQUEST(400, "R40000", "비정상적인 요청입니다."),
	INVALID_INPUT_VALUE(400, "R40001", "유효하지 않은 값입니다."),
	INVALID_TYPE_VALUE(400, "R40002", "유효하지 않은 값입니다."),
	BODY_READ_FAILED(400, "R40003", "BODY JSON parse error"),
	BIZ_DEFAULT_ERROR(400, "R40010", "처리할 수 없는 요청입니다."),
	ENCRYPTION_FAILURE(400, "R40010-001", "암호화 실패"),
	REFRESH_TOKEN_RENEW_FAILURE(400, "R40010-002", "리프래시 토큰 갱신 실패"),
	USER_CERTIFICATION_NOT_FOUND(400, "R40010-003", "사용자 인증 데이터가 없습니다."),
	USER_EMAIL_EXISTS(400, "R40010-004", "이미 사용중인 이메일입니다."),
	MBTI_ANSWER_FAILURE(400, "R40010-005", "설문지 작성에 유효하지 않은 값입니다."),
	MBTI_ANSWER_EXISTS(400, "R40010-006", "이미 MBTI 답변을 제출했습니다."),

	UNAUTHORIZED(401, "R40100", "인증이 필요한 API 입니다."),
	EXPIRED_TOKEN(401, "R40101", "만료된 토큰입니다."),
	EXPIRED_OR_INVALID_TOKEN(401, "R40102", "만료되었거나 유효하지 않은 토큰입니다."),
	INVALID_TOKEN(401, "R40103", "토큰이 유효하지 않거나, 검증에 실패했습니다."),
	JWT_TOKEN_VALIDATION_FAILURE(401, "R40104", "토큰 검증 실패"),
	JWT_TOKEN_NOT_FOUND(401, "R40105", "토큰 없음"),
	LOGIN_FAILURE(401, "R40106", "로그인을 실패했습니다. 아이디/비밀번호를 확인해 주세요."),

	ACCESS_DENIED(403, "R40300", "접근이 허용되지 않습니다."),
	NOT_FOUND(404, "R40400", "존재하지 않거나 비활성화된 API 입니다."),
	DATA_NOT_FOUND(404, "R40402", "데이터가 존재하지 않습니다."),

	METHOD_NOT_ALLOWED(405, "R40500", "허용되지 않는 Http Method 입니다."),

	UNSUPPORTED_MEDIA_TYPE(415, "R41500", "지원하지 않거나 처리할 수 없는 요청입니다."),

	INTERNAL_SERVER_ERROR(500, "R50000", "처리 중 오류가 발생했습니다."),

	EXTERNAL_API_UNAVAILABLE(503, "R50301", "서버가 요청을 처리할 준비가 되지 않았습니다."),
	;

	private final String code;
	private final String message;
	private final int status;

	ApiResponseCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}
}
