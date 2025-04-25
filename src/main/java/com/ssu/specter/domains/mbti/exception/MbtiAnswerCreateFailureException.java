package com.ssu.specter.domains.mbti.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;

public class MbtiAnswerCreateFailureException extends BusinessRuntimeException {
	public MbtiAnswerCreateFailureException(ApiResponseCode apiResponseCode, String message) {
		super(apiResponseCode, message);
	}
}
