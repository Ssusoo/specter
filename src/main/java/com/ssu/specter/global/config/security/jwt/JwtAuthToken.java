package com.ssu.specter.global.config.security.jwt;

import com.ssu.specter.global.constant.CommonConstant;
import com.ssu.specter.global.util.HttpUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class JwtAuthToken implements AuthToken<Claims> {
	@Getter
	private final String token;
	private final Key key;

	JwtAuthToken(String token, Key key) {
		this.token = token;
		this.key = key;
	}

	JwtAuthToken(JwtTokenPayload tokenPayload, Key key) {
		this.key = key;

		Optional<String> value = createJwtAuthToken(tokenPayload);
		this.token = value.orElse(null);
	}

	@Override
	public boolean validate() {
		var jwtClaims = getData();
		return jwtClaims != null;
	}

	@Override
	public Claims getData() {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	private Optional<String> createJwtAuthToken(JwtTokenPayload tokenPayload) {
		var expiredDate = Date.from(LocalDateTime.now()
				.plusMinutes(CommonConstant.Jwt.TOKEN_EXPIRED_MINUTES)
				.atZone(ZoneId.systemDefault()).toInstant());
		var host = "";
		var req = HttpUtils.getHttpServletRequest();
		if (req != null) {
			host = req.getHeader("host");
		}
		var claims = Jwts.claims();
		claims.put(JwtClaimsKey.USER_ID.getKey(), tokenPayload.userId());
		claims.put(JwtClaimsKey.USER_EMAIL.getKey(), tokenPayload.userEmail());
		claims.put(JwtClaimsKey.USER_NAME.getKey(), URLEncoder.encode(tokenPayload.userName(), StandardCharsets.UTF_8));
		claims.put(JwtClaimsKey.ROLES.getKey(), tokenPayload.roles());
		return Optional.ofNullable(Jwts.builder()
				.setIssuer(host)
				.setAudience(tokenPayload.userEmail())
				.addClaims(claims)
				.setIssuedAt(new Date())
				.setExpiration(expiredDate)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact());
	}
}
