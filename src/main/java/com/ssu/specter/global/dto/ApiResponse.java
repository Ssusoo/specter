package com.ssu.specter.global.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ssu.specter.global.constant.ApiResponseCode;
import lombok.Builder;

@JsonPropertyOrder({"code", "message", "data"})
public record ApiResponse<T>(String code, String message, T data) {
	@Builder
	public ApiResponse(final String code, final String message, final T data) {
		this.code = code == null ? ApiResponseCode.OK.getCode() : code;
		this.message = message == null ? ApiResponseCode.OK.getMessage() : message;
		this.data = data;
	}
}
