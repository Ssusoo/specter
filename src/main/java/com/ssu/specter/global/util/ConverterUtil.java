package com.ssu.specter.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"unchecked", "unused"})
public class ConverterUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,false);

	public static String convertObjectToJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

	public static Map<String, Object> convertJsonToMap(String jsonValue) throws JsonProcessingException {
		objectMapper.registerModule(new JavaTimeModule());
		if (StringUtils.hasLength(jsonValue)) {
			return objectMapper.readValue(jsonValue, Map.class);
		} else {
			return null;
		}
	}

	public static Map<String, Object> convertObjectToMap(Object object) {
		try {
			return convertJsonToMap(convertObjectToJson(object));
		} catch (Exception e) {
			return new HashMap<>();
		}
	}
}
