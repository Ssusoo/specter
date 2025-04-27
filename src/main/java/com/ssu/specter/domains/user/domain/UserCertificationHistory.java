package com.ssu.specter.domains.user.domain;

import com.ssu.specter.global.util.HttpUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SSU_SPECTER_USR_CERT_HIST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class UserCertificationHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USR_CERT_HIST_ID", nullable = false, updatable = false)
	private Long userCertificationHistoryId; // 사용자 인증 이력

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "USR_ID", nullable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@Column(name = "CERT_REQ_IP", length = 20, nullable = false, updatable = false)
	private String certRequestIp; // 인증 요청 아이피

	@Column(name = "CERT_DTM", nullable = false, updatable = false)
	private LocalDateTime certAt; // 인증 일시

	/**
	 * 인증 이력 생성
	 */
	public static UserCertificationHistory create(User user) {
		return UserCertificationHistory.builder()
				.user(user)
				.certRequestIp(HttpUtils.getClientIpAddress())
				.certAt(LocalDateTime.now())
				.build();
	}
}
