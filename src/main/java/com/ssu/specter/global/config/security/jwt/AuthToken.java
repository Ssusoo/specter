package com.ssu.specter.global.config.security.jwt;

public interface AuthToken<T> {
	boolean validate();

	T getData();
}
