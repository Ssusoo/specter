package com.ssu.specter.domains.user.domain;

import com.ssu.specter.base.domain.BaseDateEntity;
import com.ssu.specter.domains.user.domain.embed.Certification;
import com.ssu.specter.domains.user.domain.embed.UserRole;
import com.ssu.specter.domains.user.dto.UserSignUpRequest;
import com.ssu.specter.domains.user.dto.payload.UserCreatePayload;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SSU_SPECTER_USR")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User extends BaseDateEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USR_ID", nullable = false, updatable = false)
	private Long userId; // 사용자 아이디

	@Column(name = "USR_EMAIL", length = 50, nullable = false, updatable = false, unique = true)
	private String userEmail; // 이메일

	@Column(name = "USR_PASSWORD", nullable = false)
	private String userPassword; // 비밀번호

	@Column(name = "USR_NAME", length = 100, nullable = false)
	private String userName; // 이름

	@Column(name = "USR_PHONE", nullable = false)
	private String userPhone; // 전화번호

	@Enumerated(EnumType.STRING)
	@Column(name = "USR_ROLE", nullable = false)
	private UserRole role; // 권한

	@Embedded
	private Certification certification; // 인증

	@OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	private final List<UserCertificationHistory> userCertificationHistories = new ArrayList<>(); // 사용자 인증 이력

	/**
	 * 회원 가입
	 * @param request : 회원 가입 요청 DTO {@link UserSignUpRequest}
	 * @return
	 */
	public static User signUp(UserCreatePayload request, PasswordEncoder passwordEncoder) {
		return User.builder()
				.userEmail(request.userEmail())
				.userPassword(passwordEncoder.encode(request.userPassword()))
				.userName(request.userName())
				.userPhone(request.userPhone())
				.role(request.userRole())
				.build();
	}

	/**
	 * 로그인
	 */
	public void login() {
		if (certification == null) { // 최초 로그인
			certification = Certification.builder().build();
		}
		certification.cert(); // 인증 처리
		userCertificationHistories.add(UserCertificationHistory.create(this)); // 인증 이력 추가
	}

	/**
	 * 로그아웃
	 */
	public void logout() {
		if (certification == null) {
			return;
		}
		certification.revokeRefreshToken();
	}

	/**
	 * RefreshToken 반환
	 */
	public final String getRefreshToken() {
		if (certification == null) {
			return null;
		}
		return certification.getRefreshToken();
	}
}
