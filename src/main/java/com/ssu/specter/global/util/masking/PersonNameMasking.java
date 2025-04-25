package com.ssu.specter.global.util.masking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 사람 이름 마스킹
 */
public class PersonNameMasking implements StringValueMasking {

	public static PersonNameMasking getInstance() {
		return new PersonNameMasking();
	}

	@Override
	public String apply(String originValue) {
		try {
			var personName = originValue.replace(" ", ""); // 공백 제거
			var regex = "(^[가-힣]+)"; // 한글 이름만 처리
			Matcher matcher = Pattern.compile(regex).matcher(personName);
			if (matcher.find()) {
				var notMaskingTargetValue = getNotMaskingTargetValue(regex, personName);
				personName = personName.replace(notMaskingTargetValue, "");

				int length = personName.length();
				var middleMask = "";
				if (length > 2) {
					middleMask = personName.substring(1, length - 1);
				} else { // 이름이 외자
					middleMask = personName.substring(1, length);
				}
				var middle = middleMask.replace(middleMask, "*".repeat(middleMask.length()));
				if (length > 2) {
					return personName.charAt(0) + middle + personName.charAt(length - 1) + notMaskingTargetValue;
				} else { // 이름이 외자 마스킹 리턴
					return personName.charAt(0) + middle + notMaskingTargetValue;
				}
			}
			/*
			 * 3자리 이상인 경우 앞 3자리 제외 마스킹
			 * 3자리 이하인 경우 앞 1자리 제외 마스킹, 외자 1자리 : *
			 */
			var regexEng = "(^[a-zA-Z]+)";  // 영문 이름만 처리
			Matcher matcherEng = Pattern.compile(regexEng).matcher(personName);
			if (matcherEng.find()) {
				int length = personName.length();
				if (length > 3) {
					return personName.substring(0, 3) + "*".repeat(length-3);
				} else if (length > 1) {
					return personName.charAt(0) + "*".repeat(length-1);
				} else { // 이름이 외자 마스킹 리턴
					return "*".repeat(length);
				}
			}
		} catch (Exception ignored) {
			// ignored
		}
		return originValue;
	}
}
