package com.ssu.specter.domains.user.application;

import com.ssu.specter.domains.user.application.adapter.UserAdapter;
import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.dto.AccessTokenRenewRequest;
import com.ssu.specter.domains.user.dto.AccessTokenResult;
import com.ssu.specter.domains.user.dto.UserLoginRequest;
import com.ssu.specter.domains.user.exception.RefreshTokenRenewFailureException;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.global.config.security.jwt.JwtAuthTokenProvider;
import com.ssu.specter.global.config.security.jwt.JwtClaimsPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthService {
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final UserRepository userRepository;
	private final JwtAuthTokenProvider jwtAuthTokenProvider;

	/**
	 * 로그인 처리
	 * @param request : 로그인 요청 DTO {@link UserLoginRequest}
	 */
	@Transactional
	public AccessTokenResult login(UserLoginRequest request) {
		// 사용자 인증정보 생성, 조회 실패 시 Exception 발생 및 이후 로직 실행 안됨
		var authenticationToken = new UsernamePasswordAuthenticationToken(request.userEmail(), request.userPassword());
		var authentication = authenticationManagerBuilder.getObject()
				.authenticate(authenticationToken);

		// 인증을 성공했다면 Jwt Claims 를 만들기 위한 정보를 가져온다.
		var userAdapter = ((UserAdapter) authentication.getPrincipal());
		var user = userAdapter.getUser(); // 사용자 조회
		user.login(); // 로그인 처리
		return createResult(userAdapter);
	}

	/**
	 * 로그아웃 처리
	 */
	@Transactional
	public void logout() {
		userRepository.findUser(JwtClaimsPayload.getUserId())
				.ifPresent(User::logout);
	}

	/**
	 * AccessToken 갱신
	 * @param request : AccessToken 갱신 요청 DTO {@link AccessTokenRenewRequest}
	 */
	@Transactional
	public AccessTokenResult renewAccessToken(AccessTokenRenewRequest request) {
		var user = userRepository.findUser(request.userEmail())
				.orElseThrow(RefreshTokenRenewFailureException::new);
		// 리프레시 토큰 검증
		if (!ObjectUtils.nullSafeEquals(user.getRefreshToken(), request.refreshToken())) {
			throw new RefreshTokenRenewFailureException();
		}
		user.getCertification().renewRefreshToken(); // 리프래시 토큰 갱신
		return createResult(new UserAdapter(user));
	}

	private AccessTokenResult createResult(UserAdapter userAdapter) {
		var accessToken = jwtAuthTokenProvider.createAuthToken(userAdapter.getJwtTokenPayload()).getToken();
		var certification = userAdapter.getUser().getCertification(); // 사용자 인증 조회
		return AccessTokenResult.builder()
				.accessToken(accessToken)
				.refreshToken(certification.getRefreshToken())
				.refreshTokenExpireAt(certification.getRefreshTokenExpireAt())
				.build();
	}
}
