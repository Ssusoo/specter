package com.ssu.specter.global.util.masking;

import org.apache.commons.lang3.StringUtils;

/**
 * 이메일 마스킹
 */
public class EmailMasking implements StringValueMasking {
	public static EmailMasking getInstance() {
		return new EmailMasking();
	}

	/**
	 * 앞 3자리 제외 마스킹
	 * @param originValue : zzanggoon8@admin.com
	 * @return : zza*********@admin.com
	 */
	@Override
	public String apply(String originValue) {
		try {
			if (StringUtils.isEmpty(originValue)) {
				return originValue;
			}
			String[] originEmail = originValue.split("@");
			var originEmailId = originEmail[0];
			int length = originEmailId.length();
			if (length > 3) {
				return originValue.replaceAll("(?<=.{3})(?=.{0,3}@).(?=.*@)", "*");
			} else {
				var middleMask = "";
				middleMask = originEmailId.substring(1, length);
				var middle = middleMask.replace(middleMask, "*".repeat(middleMask.length()));
				return originValue.charAt(0) + middle + "@" + originEmail[1];
			}
		} catch (Exception ignored) {
			// ignored
		}
		return originValue;
	}
}
