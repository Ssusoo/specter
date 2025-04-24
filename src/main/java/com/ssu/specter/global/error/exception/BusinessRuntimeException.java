package com.ssu.specter.global.error.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import lombok.Getter;

@SuppressWarnings({"serial", "RedundantSuppression"})
public class BusinessRuntimeException extends RuntimeException {
	private final ApiResponseCode apiResponseCode;
	@Getter
	private final String errorMessage;

	public BusinessRuntimeException(ApiResponseCode apiResponseCode) {
		super(apiResponseCode.getMessage());
		this.apiResponseCode = apiResponseCode;
		this.errorMessage = apiResponseCode.getMessage();
	}

	public BusinessRuntimeException(ApiResponseCode apiResponseCode, String message) {
		super(message);
		this.apiResponseCode = apiResponseCode;
		this.errorMessage = message;
	}

	public ApiResponseCode getErrorCode() {
		return apiResponseCode;
	}
}
