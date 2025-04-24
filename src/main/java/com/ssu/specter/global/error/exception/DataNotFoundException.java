package com.ssu.specter.global.error.exception;

import com.ssu.specter.global.constant.ApiResponseCode;

public class DataNotFoundException extends BusinessRuntimeException {
	public DataNotFoundException() {
		super(ApiResponseCode.DATA_NOT_FOUND);
	}
}
