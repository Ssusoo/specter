package com.ssu.specter.domains.mbti.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssu.specter.base.controller.BaseController;
import com.ssu.specter.domains.mbti.application.MbtiQuestionCreateService;
import com.ssu.specter.domains.mbti.application.MbtiQuestionFindService;
import com.ssu.specter.domains.mbti.dto.MbtiCreateRequest;
import com.ssu.specter.domains.mbti.dto.MbtiQuestionDetailFindResult;
import com.ssu.specter.domains.mbti.dto.MbtiQuestionFindResult;
import com.ssu.specter.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "성격 유형 검사 설문")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mbtis")
public class MbtiQuestionController extends BaseController {
	private final MbtiQuestionFindService mbtiQuestionFindService;
	private final MbtiQuestionCreateService mbtiQuestionCreateService;

	@GetMapping("/questions")
	@Operation(summary = "목록")
	public ApiResponse<List<MbtiQuestionFindResult>> getMbtiQuestions() {
		return ok(mbtiQuestionFindService.getMbtiQuestions());
	}

	@GetMapping("/{userId}")
	@Operation(summary = "상세")
	public ApiResponse<MbtiQuestionDetailFindResult> getMbtiQuestionDetail(@PathVariable Long userId) throws JsonProcessingException {
		return ok(mbtiQuestionFindService.getMbtiQuestionDetail(userId));
	}

	@PostMapping("/complete")
	@Operation(summary = "등록")
	public ApiResponse<Object> create(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(schema = @Schema(implementation = MbtiCreateRequest.class))
	)
	                                      @Valid @RequestBody MbtiCreateRequest request) throws JsonProcessingException {
		mbtiQuestionCreateService.create(request);
		return ok();
	}
}
