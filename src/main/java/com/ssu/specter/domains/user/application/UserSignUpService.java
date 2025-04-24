package com.ssu.specter.domains.user.application;

import com.ssu.specter.domains.user.domain.User;
import com.ssu.specter.domains.user.dto.UserSignUpRequest;
import com.ssu.specter.domains.user.dto.payload.UserCreatePayload;
import com.ssu.specter.domains.user.exception.EmailAlreadyExistsException;
import com.ssu.specter.domains.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserSignUpService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 가입 처리
	 * @param request : 가입 요청 DTO {@link UserSignUpRequest}
	 */
	@Transactional
	public void signUp(UserSignUpRequest request) {
		// 이메일 중복 확인
		if (userRepository.existsByUserEmail(request.userEmail())) {
			throw new EmailAlreadyExistsException();
		}

		// 암호화 전 Payload 생성
		var payload = new UserCreatePayload(request);

		// 암호화 포함 User 엔티티 생성
		var user = User.signUp(payload, passwordEncoder);

		// 저장
		userRepository.save(user);
	}
}
