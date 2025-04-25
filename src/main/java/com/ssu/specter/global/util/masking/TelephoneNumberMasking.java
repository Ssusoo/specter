package com.ssu.specter.global.util.masking;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 전화번호 (휴대폰번호) 마스킹
 */
@Validated
public class TelephoneNumberMasking implements StringValueMasking {
	public static TelephoneNumberMasking getInstance() {
		return new TelephoneNumberMasking();
	}

	@Override
	public String apply(String originValue) {
		return apply(originValue, MaskingStrategy.MIDDLE_PART);
	}

	@Override
	public String apply(String originValue, @NotNull MaskingStrategy strategy) {
		try {
			if (StringUtils.isEmpty(originValue)) {
				return originValue;
			}
			String regex = "^(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";
			Matcher matcher = Pattern.compile(regex).matcher(originValue);
			if (matcher.find()) {
				if (MaskingStrategy.MIDDLE_PART.equals(strategy)) {
					return matcher.replaceAll("$1-" + getMaskedValue(matcher.group(2)) + "-$3");
				} else if (MaskingStrategy.BACK_PART.equals(strategy)) {
					return matcher.replaceAll("$1-$2-" + getMaskedValue(matcher.group(3)));
				}
			}
		} catch (Exception ignored) {
			// ignored
		}
		return originValue;
	}
}
