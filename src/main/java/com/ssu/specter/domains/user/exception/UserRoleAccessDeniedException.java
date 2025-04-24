package com.ssu.specter.domains.user.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;

public class UserRoleAccessDeniedException extends BusinessRuntimeException {
	public UserRoleAccessDeniedException() {
		super(ApiResponseCode.ACCESS_DENIED);
	}
}
