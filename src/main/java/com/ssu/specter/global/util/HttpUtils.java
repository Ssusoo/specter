package com.ssu.specter.global.util;

import com.ssu.specter.global.error.exception.JwtTokenNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {
	/**
	 * Gets the http servlet request.
	 *
	 * @return the http servlet request
	 */
	public static HttpServletRequest getHttpServletRequest() {
		var servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (servletRequestAttributes != null) {
			return servletRequestAttributes.getRequest();
		} else {
			return null;
		}
	}

	public static String getBearerJwtToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			// "Bearer eyJ...","refreshToken":"... 처럼 잘못 붙은 경우를 대비
			String token = authHeader.substring(7); // "Bearer " 제거

			// 쌍따옴표, 쉼표, 콜론 이후 부분 제거
			token = token.split("[\",:]")[0]; // 첫 번째 토큰만 사용

			return token.trim();
		}

		throw new JwtTokenNotFoundException();
	}


	/**
	 * Gets the ip addr.
	 */
	public static String getClientIpAddress() {
		var request = getHttpServletRequest();
		if (request == null) {
			return "0.0.0.0";
		}
		return getClientIpAddress(request);
	}

	public static String getClientIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		var unknown = "unknown";
		if (StringUtils.isNotEmpty(ip) && !unknown.equalsIgnoreCase(ip)) {
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("Proxy-Client-IP");
		if (StringUtils.isNotEmpty(ip) && !unknown.equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (StringUtils.isNotEmpty(ip) && !unknown.equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("HTTP_CLIENT_IP");
		if (StringUtils.isNotEmpty(ip) && !unknown.equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (StringUtils.isNotEmpty(ip) && !unknown.equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}
}
