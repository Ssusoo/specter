package com.ssu.specter.global.constant.code;

import com.fasterxml.jackson.annotation.JsonValue;

public interface CodeEnum {
	@JsonValue
	String getCode();
	String getCodeName();

	static String getCode(CodeEnum codeEnum) {
		return codeEnum != null ? codeEnum.getCode() : null;
	}

	static String getCodeName(CodeEnum codeEnum) {
		return codeEnum != null ? codeEnum.getCodeName() : null;
	}
}
