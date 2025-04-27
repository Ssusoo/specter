package com.ssu.specter.global.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Yn {
		public static final String Y = "Y";
		public static final String N = "N";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Jwt {
		public static final long TOKEN_EXPIRED_MINUTES = 20L; // 인증토큰 유효 시간 (분)
		public static final long REFRESH_TOKEN_EXPIRE_HOURS = 3L; // refreshToken 유효 시간 (시)
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Type {
		public static final String RADIO = "radio";
		public static final String TEXTAREA = "textarea";
	}
}
