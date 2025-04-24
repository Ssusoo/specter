package com.ssu.specter.domains.user.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;

public class EmailAlreadyExistsException extends BusinessRuntimeException {
	public EmailAlreadyExistsException() {
		super(ApiResponseCode.USER_EMAIL_EXISTS);
	}
}
