package com.ssu.specter.domains.user.controller;

import com.ssu.specter.base.controller.BaseController;
import com.ssu.specter.domains.user.application.UserSignUpService;
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
}
