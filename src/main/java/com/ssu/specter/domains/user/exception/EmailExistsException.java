package com.ssu.specter.domains.user.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;

public class EmailExistsException extends BusinessRuntimeException {
	public EmailExistsException() {
		super(ApiResponseCode.USER_EMAIL_EXISTS);
	}
}
