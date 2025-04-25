package com.ssu.specter.domains.mbti.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ssu_mbti_qst")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class MbtiQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "qst_id", nullable = false, updatable = false)
	private Long questionId; // 성격 유형 검사 설문 번호

	@Column(name = "qst_nm", nullable = false, length = 100)
	private String question; // 성격 유형 검사 설문

	@Column(name = "qst_type", nullable = false)
	private String type; // 타입
}
