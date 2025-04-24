package com.ssu.specter.global.config.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	private final JwtAuthTokenProvider jwtAuthTokenProvider;

	@Override
	public void configure(HttpSecurity http) {
		http.addFilterBefore(new JwtAuthenticationFilter(jwtAuthTokenProvider), UsernamePasswordAuthenticationFilter.class);
	}
}
