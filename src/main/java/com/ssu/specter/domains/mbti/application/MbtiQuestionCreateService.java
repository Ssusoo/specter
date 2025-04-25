package com.ssu.specter.domains.mbti.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssu.specter.domains.mbti.domain.MbtiAnswer;
import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import com.ssu.specter.domains.mbti.dto.MbtiCreateRequest;
import com.ssu.specter.domains.mbti.exception.MbtiAnswerCreateFailureException;
import com.ssu.specter.domains.mbti.repository.MbtiAnswerRepository;
import com.ssu.specter.domains.mbti.repository.MbtiQuestionRepository;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.exception.MbtiAnswerExistsException;
import com.ssu.specter.domains.user.exception.UserRoleAccessDeniedException;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.global.config.security.jwt.JwtClaimsPayload;
import com.ssu.specter.global.constant.ApiResponseCode;
import com.ssu.specter.global.error.exception.DataNotFoundException;
import com.ssu.specter.global.util.ConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbtiQuestionCreateService {
	private final MbtiAnswerRepository mbtiAnswerRepository;
	private final MbtiQuestionRepository mbtiQuestionRepository;
	private final UserRepository userRepository;

	@Transactional
	public void create(MbtiCreateRequest request) throws JsonProcessingException {
		var userEmail = JwtClaimsPayload.getUserEmail();
		var user = userRepository.findUser(userEmail).orElseThrow(DataNotFoundException::new);

		if (!UserRole.USER.equals(user.getRole())) {
			throw new UserRoleAccessDeniedException();
		}

		var answers = request.mbtiAnswerInfoList();

		// 중복 답변 체크
		if (mbtiAnswerRepository.existsByAnswer(user.getUserId())) {
			throw new MbtiAnswerExistsException();
		}

		// 성격 유형 검사 답변 개수 검증
		if (answers == null || answers.size() != 20) {
			throw new MbtiAnswerCreateFailureException(ApiResponseCode.MBTI_ANSWER_FAILURE, "모든 질문(20개)에 응답해주세요.");
		}

		// 성격 유형 검사 목록
		var mbtiQuestions = mbtiQuestionRepository.findMbtiQuestions();

		// 질문 타입 정리
		Map<Long, String> questionTypeMap = mbtiQuestions.stream()
				.collect(Collectors.toMap(MbtiQuestion::getQuestionId, MbtiQuestion::getType));

		// 성격 유형 검사 답변 유효성 검사
		for (var answer : answers) {
			var questionId = answer.questionId();
			var answerValue = answer.answer();

			var type = questionTypeMap.get(questionId);

			if ("radio".equals(type)) {
				if (!"Y".equals(answerValue) && !"N".equals(answerValue)) {
					throw new MbtiAnswerCreateFailureException(
							ApiResponseCode.MBTI_ANSWER_FAILURE,
							"Y 또는 N만 입력 가능합니다. (설문지: " + questionId + "번)"
					);
				}
			}

			if ("textarea".equals(type)) {
				if (answerValue == null || answerValue.trim().isEmpty()) {
					throw new MbtiAnswerCreateFailureException(
							ApiResponseCode.MBTI_ANSWER_FAILURE,
							"텍스트를 입력해주세요. (설문지 : " +  questionId + "번)"
					);
				}
			}
		}
		mbtiAnswerRepository.save(MbtiAnswer.create(user, ConverterUtil.convertObjectToJson(answers)));
	}
}
