package com.ssu.specter.domains.user.application.impl;

import com.ssu.specter.domains.user.application.adapter.UserAdapter;
import com.ssu.specter.domains.user.repository.UserRepository;
import com.ssu.specter.global.constant.ApiResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		var user = userRepository.findUser(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException(ApiResponseCode.LOGIN_FAILURE.getMessage())); // 조회 불가능한 아이디 예외
		return new UserAdapter(user);
	}
}
