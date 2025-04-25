package com.ssu.specter.global.util.masking;

import org.apache.commons.lang3.StringUtils;

public interface StringValueMasking extends Masking<String> {
	default String getNotMaskingTargetValue(String regex, String value) {
		return value.replaceAll(regex, "");
	}

	default String getMaskedValue(String value) {
		if (StringUtils.isEmpty(value)) {
			return "";
		}
		return "*".repeat(value.length());
	}

	default String getMaskedValue(int length) {
		if (length == 0) {
			return "";
		}
		return "*".repeat(length);
	}
}
