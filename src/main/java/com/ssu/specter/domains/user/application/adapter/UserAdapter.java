package com.ssu.specter.domains.user.application.adapter;

import com.ssu.specter.global.config.security.jwt.JwtTokenPayload;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAdapter extends User {
	private final transient com.ssu.specter.domains.user.domain.User user;
	private final transient JwtTokenPayload jwtTokenPayload;

	public UserAdapter(com.ssu.specter.domains.user.domain.User user) {
		super(user.getUserEmail(), user.getUserPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
		this.user = user;
		this.jwtTokenPayload = JwtTokenPayload.builder()
				.userId(user.getUserId())
				.userEmail(user.getUserEmail())
				.userName(user.getUserName())
				.roles(List.of("ROLE_" + user.getRole()))
				.build();
	}
}
