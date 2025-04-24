package com.ssu.specter.domains.user.application;

import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.dto.UserSignUpRequest;
import com.ssu.specter.domains.user.exception.EmailAlreadyExistsException;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.test.MockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

class UserSignUpServiceTest extends MockTest {
	@InjectMocks
	private UserSignUpService userSignUpService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void beforeEach() {
		lenient().when(passwordEncoder.encode(anyString())).thenReturn("encoded-string");
		lenient().when(userRepository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
	}

	@Test
	@DisplayName("가입 처리 - 성공")
	void signUp_success() {
		// given
		var request = getUserSignUpRequest();
		given(userRepository.existsByUserEmail(anyString())).willReturn(false);

		// when
		userSignUpService.signUp(request);

		// then
		assertTrue(true);
	}

	@Test
	@DisplayName("가입 처리 - 실패(중복 이메일)")
	void signUp_fail_exists_email() {
		// given
		var request = getUserSignUpRequest();
		given(userRepository.existsByUserEmail(anyString())).willReturn(true);

		// when & then
		assertThrows(EmailAlreadyExistsException.class,
				() -> userSignUpService.signUp(request));

	}

	private UserSignUpRequest getUserSignUpRequest() {
		return UserSignUpRequest.builder()
				.userEmail("test@test.com")
				.userPassword("1234")
				.userName("김테스트")
				.userPhone("010-1234-1234")
				.role(UserRole.USER)
				.build();
	}
}
