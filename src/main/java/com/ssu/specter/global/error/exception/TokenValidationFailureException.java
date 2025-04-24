package com.ssu.specter.global.error.exception;

import com.ssu.specter.global.constant.ApiResponseCode;

@SuppressWarnings({"serial", "RedundantSuppression"})
public class TokenValidationFailureException extends BusinessRuntimeException {

	public TokenValidationFailureException() {
		super(ApiResponseCode.JWT_TOKEN_VALIDATION_FAILURE);
	}
}
