package com.ssu.specter.global.config.security;

import com.ssu.specter.global.config.security.jwt.JwtAccessDeniedHandler;
import com.ssu.specter.global.config.security.jwt.JwtAuthenticationEntryPoint;
import com.ssu.specter.global.config.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 보안 설정 구성
 *  1) 클라이언트가 JWT 포함해서 API 요청. -> 2) JwtAuthenticationFilter가 필터링(토큰이 유효한지 검증 → 인증 성공 시 SecurityContext에 등록.)
 *  3) 인증 실패 시 → JwtAuthenticationEntryPoint 작동.
 *  4) 권한 부족 시 → JwtAccessDeniedHandler 작동.
 *  5) Spring Security 설정에서 이 흐름을 모두 조립해 둠.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (JWT는 토큰 기반이라 필요 없음)
				.cors(cors -> corsConfigurationSource()) // CORS 허용 설정 (*로 모든 Origin과 Method 허용)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
				.formLogin(AbstractHttpConfigurer::disable) // 비활성화 → RESTful API 스타일로 인증
				.httpBasic(AbstractHttpConfigurer::disable) // 비활성화 → RESTful API 스타일로 인증
				.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 필터 추가

				// 위에서 만든 EntryPoint, AccessDeniedHandler 등록
				.exceptionHandling(exceptionHandling ->
						exceptionHandling
								.authenticationEntryPoint(jwtAuthenticationEntryPoint)
								.accessDeniedHandler(jwtAccessDeniedHandler))
				// 경로별 인증/비인증 구분
				.authorizeHttpRequests(
						authorize -> authorize
								.requestMatchers(
										"/specter/signup", "/specter/login", "/specter/renew-access-token",
										"/spctr/**", "/h2-console/**", "/v3/api-docs/**"
								).permitAll()
								.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
								.anyRequest().authenticated()
				)
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedHeader("*");
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
