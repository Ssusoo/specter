package com.ssu.specter.domains.user.controller;

import com.ssu.specter.base.controller.BaseController;
import com.ssu.specter.domains.user.application.UserAuthService;
import com.ssu.specter.domains.user.application.UserSignUpService;
import com.ssu.specter.domains.user.dto.AccessTokenRenewRequest;
import com.ssu.specter.domains.user.dto.AccessTokenResult;
import com.ssu.specter.domains.user.dto.UserLoginRequest;
import com.ssu.specter.domains.user.dto.UserSignUpRequest;
import com.ssu.specter.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원")
@RestController
@RequiredArgsConstructor
@RequestMapping("/specter")
public class UserController extends BaseController {
	private final UserSignUpService userSignUpService;
	private final UserAuthService userAuthService;

	@Operation(summary = "회원 가입")
	@PostMapping("/signup")
	public ApiResponse<Object> signUp(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(schema = @Schema(implementation = UserSignUpRequest.class))
			)
			@Valid @RequestBody UserSignUpRequest request) {
		userSignUpService.signUp(request);
		return ok();
	}

	@Operation(summary = "로그인")
	@PostMapping("/login")
	public ApiResponse<AccessTokenResult> login(@Valid @RequestBody UserLoginRequest request) {
		return ok(userAuthService.login(request));
	}

	@Operation(summary = "로그아웃")
	@PostMapping("/logout")
	public ApiResponse<Object> logout() {
		userAuthService.logout();
		return ok();
	}

	@Operation(summary = "AccessToken 갱신")
	@PostMapping("/renew-access-token")
	public ApiResponse<AccessTokenResult> renewAccessToken(@Valid @RequestBody AccessTokenRenewRequest request) {
		return ok(userAuthService.renewAccessToken(request));
	}
}
