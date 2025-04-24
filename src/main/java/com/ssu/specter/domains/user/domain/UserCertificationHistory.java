package com.ssu.specter.domains.user.domain;

import com.ssu.specter.global.util.HttpUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ssu_specter_usr_cert_hist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class UserCertificationHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usr_cert_hist_serlno", nullable = false, updatable = false)
	private Long userCertificationHistorySerialNo; // 사용자 인증 이력 일련번호

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usr_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@Column(name = "cert_req_ip", length = 20, nullable = false, updatable = false)
	private String certRequestIp; // 인증 요청 아이피

	@Column(name = "cert_dtm", nullable = false, updatable = false)
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
