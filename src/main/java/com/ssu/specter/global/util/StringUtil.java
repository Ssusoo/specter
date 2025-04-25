package com.ssu.specter.global.util;

public class StringUtil {

	/**
	 * Instantiates a new string util.
	 */
	private StringUtil() {
	}

	/**
	 * Checks if is empty.
	 *
	 * @param value the value
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String value) {
		return (value == null || value.length() == 0);
	}
}
