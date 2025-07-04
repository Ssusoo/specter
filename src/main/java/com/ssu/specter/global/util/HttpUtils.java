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
		String jwtToken = request.getHeader("Authorization");

		if (org.springframework.util.StringUtils.hasText(jwtToken)) {
			if (!jwtToken.startsWith("Bearer ")) {
				throw new JwtTokenNotFoundException();
			}

			// Bearer 접두사 제거 후 공백 제거
			String token = jwtToken.substring(7).trim();

			// 따옴표로 감싸져 있는 경우 제거
			if (token.startsWith("\"") && token.endsWith("\"")) {
				token = token.substring(1, token.length() - 1);
			}

			// 쉼표나 여분의 문자열이 포함될 경우 앞쪽만 추출
			int commaIndex = token.indexOf(',');
			if (commaIndex != -1) {
				token = token.substring(0, commaIndex);
			}

			return token;
		}
		return null;
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
