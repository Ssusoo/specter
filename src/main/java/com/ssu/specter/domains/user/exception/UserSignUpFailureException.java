package com.ssu.specter.domains.user.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;

public class UserSignUpFailureException extends BusinessRuntimeException {
	public UserSignUpFailureException() {
		super(ApiResponseCode.USER_SIGNUP_FAILURE);
	}
}

