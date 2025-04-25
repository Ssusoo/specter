package com.ssu.specter.global.util.masking;

import com.ssu.specter.global.util.masking.MaskingStrategy;

public interface Masking<T> {
	default T apply(T originValue) {
		return originValue;
	}

	@SuppressWarnings("unused")
	default T apply(T originValue, MaskingStrategy strategy) {
		return originValue;
	}
}