package com.ssu.specter.global.config.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
	@Value("${env.jwt-secret}")
	private String jwtSecret;

	@Bean
	public JwtAuthTokenProvider jwtProvider() {
		return new JwtAuthTokenProvider(jwtSecret);
	}
}

