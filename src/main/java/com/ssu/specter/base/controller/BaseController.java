package com.ssu.specter.base.controller;

import com.ssu.specter.global.dto.ApiResponse;

@SuppressWarnings({"unchecked", "unused"})
public abstract class BaseController {

	/**
	 * 정상 (200) 응답
	 */
	public ApiResponse<Object> ok() {
		return ok(null, null);
	}

	/**
	 * 정상 (200) 응답
	 *
	 * @param data : 응답 클래스
	 */
	public <T> ApiResponse<T> ok(T data) {
		return ok(data, null);
	}

	/**
	 * 정상 (200) 응답
	 *
	 * @param message : 응답 결과 메세지
	 */
	public <T> ApiResponse<T> ok(String message) {
		return ok(null, message);
	}

	public <T> ApiResponse<T> ok(T data, String message) {
		return (ApiResponse<T>) ApiResponse.builder()
				.message(message)
				.data(data)
				.build();
	}
}
