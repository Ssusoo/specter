package com.ssu.specter.domains.mbti.domain;

import com.ssu.specter.domains.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SSU_MBTI_ANS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class MbtiAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ANS_ID", nullable = false, updatable = false)
	private Long answerId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "USR_ID", nullable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@Column(name = "ANS_DATA", columnDefinition = "TEXT", nullable = false)
	private String answerData; // JSON으로 저장

	public static MbtiAnswer create(User user, String answerJson) {
		return MbtiAnswer.builder()
				.user(user)
				.answerData(answerJson)
				.build();
	}
}
