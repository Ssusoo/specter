package com.ssu.specter.domains.mbti.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssu.specter.domains.mbti.domain.MbtiQuestion;
import com.ssu.specter.domains.mbti.dto.MbtiQuestionDetailFindResult;
import com.ssu.specter.domains.mbti.dto.MbtiQuestionFindResult;
import com.ssu.specter.domains.mbti.repository.MbtiAnswerRepository;
import com.ssu.specter.domains.mbti.repository.MbtiQuestionRepository;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.exception.UserRoleAccessDeniedException;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.global.config.security.jwt.JwtClaimsPayload;
import com.ssu.specter.global.error.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbtiQuestionFindService {
	private final UserRepository userRepository;
	private final MbtiQuestionRepository mbtiQuestionRepository;
	private final MbtiAnswerRepository mbtiAnswerRepository;

	/**
	 * 성격 유형 검사 설문 - 목록
	 */
	public List<MbtiQuestionFindResult> getMbtiQuestions() {
		var userEmail = JwtClaimsPayload.getUserEmail();
		var user = userRepository.findUser(userEmail).orElseThrow(DataNotFoundException::new);

		// 유저 권한 체크
		if (!UserRole.USER.equals(user.getRole())) {
			throw new UserRoleAccessDeniedException();
		}
		List<MbtiQuestion> mbtiQuestions = mbtiQuestionRepository.findMbtiQuestions();
		return mbtiQuestions.stream().map(MbtiQuestionFindResult::new)
				.collect(Collectors.toList());
	}

	/**
	 * 성격 유형 검사 설문 - 상세
	 */
	public MbtiQuestionDetailFindResult getMbtiQuestionDetail(Long userId) throws JsonProcessingException {
		var userEmail = JwtClaimsPayload.getUserEmail();
		var user = userRepository.findUser(userEmail).orElseThrow(DataNotFoundException::new);

		// 관리자 권한 체크
		if (!UserRole.MANAGER.equals(user.getRole())) {
			throw new UserRoleAccessDeniedException();
		}

		// 성격 유형 검사 설문지 답변 조회
		var mbtiAnswer = mbtiAnswerRepository
				.findMbtiAnswerDetail(userId).orElseThrow(DataNotFoundException::new);

		List<MbtiQuestionDetailFindResult.MbtiAnswerInfo> answerList =
				new ObjectMapper().readValue(
						mbtiAnswer.getAnswerData(),
						new TypeReference<>() {}
				);

		return MbtiQuestionDetailFindResult.builder()
				.answerId(mbtiAnswer.getAnswerId())
				.userId(mbtiAnswer.getUser().getUserId())
				.userEmail(mbtiAnswer.getUser().getUserEmail())
				.userName(mbtiAnswer.getUser().getUserName())
				.userPhone(mbtiAnswer.getUser().getUserPhone())
				.answerData(answerList)
				.build();
	}
}
