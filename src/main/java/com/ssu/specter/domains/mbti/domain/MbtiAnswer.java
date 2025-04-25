package com.ssu.specter.domains.mbti.domain;

import com.ssu.specter.domains.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ssu_mbti_ans")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class MbtiAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ans_id", nullable = false, updatable = false)
	private Long answerId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usr_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@Column(name = "ans_data", columnDefinition = "TEXT", nullable = false)
	private String answerData; // JSON으로 저장

	public static MbtiAnswer create(User user, String answerJson) {
		return MbtiAnswer.builder()
				.user(user)
				.answerData(answerJson)
				.build();
	}
}
