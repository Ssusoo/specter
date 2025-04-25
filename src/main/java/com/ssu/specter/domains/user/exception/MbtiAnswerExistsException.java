package com.ssu.specter.domains.user.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;

public class MbtiAnswerExistsException extends BusinessRuntimeException {
	public MbtiAnswerExistsException() {
		super(ApiResponseCode.MBTI_ANSWER_EXISTS);
	}
}
