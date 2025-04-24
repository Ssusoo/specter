package com.ssu.specter.domains.user.exception;

import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.BusinessRuntimeException;

public class RefreshTokenRenewFailureException extends BusinessRuntimeException {
	public RefreshTokenRenewFailureException() {
		super(ApiResponseCode.REFRESH_TOKEN_RENEW_FAILURE);
	}
}
