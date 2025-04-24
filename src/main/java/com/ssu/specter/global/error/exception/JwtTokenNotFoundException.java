package com.ssu.specter.global.error.exception;

import com.ssu.specter.global.constant.ApiResponseCode;

public class JwtTokenNotFoundException extends BusinessRuntimeException {
	public JwtTokenNotFoundException() {
		super(ApiResponseCode.JWT_TOKEN_NOT_FOUND);
	}
}
