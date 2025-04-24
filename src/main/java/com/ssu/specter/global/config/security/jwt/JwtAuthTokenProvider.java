package com.ssu.specter.global.config.security.jwt;

import com.ssu.specter.global.error.exception.TokenValidationFailureException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class JwtAuthTokenProvider implements AuthTokenProvider<JwtAuthToken> {
	private final Key key;

	public JwtAuthTokenProvider(String jwtSecret) {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	@Override
	public JwtAuthToken createAuthToken(JwtTokenPayload tokenPayload) {
		return new JwtAuthToken(tokenPayload, key);
	}

	@Override
	public JwtAuthToken convertAuthToken(String token) {
		return new JwtAuthToken(token, key);
	}

	@Override
	public Authentication getAuthentication(JwtAuthToken authToken) {
		if (authToken.validate()) {
			var claims = authToken.getData();
			Collection<? extends GrantedAuthority> authorities = Arrays
					.stream(new String[]{claims.get(JwtClaimsKey.ROLES.getKey()).toString()}).map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());

			var principal = new User(claims.getAudience(), "", authorities);
			return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
		} else {
			throw new TokenValidationFailureException();
		}
	}
}
