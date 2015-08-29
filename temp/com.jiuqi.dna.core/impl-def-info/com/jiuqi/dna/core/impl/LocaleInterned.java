package com.jiuqi.dna.core.impl;

import java.util.Locale;

final class LocaleInterned {

	private static int buildKey(char c1, char c2, char c3, char c4) {
		if (c1 >= 0xff) {
			return -1;
		}
		if (c2 > 0xff) {
			return -1;
		}
		if (c3 > 0xff) {
			return -1;
		}
		if (c4 >= 0xff) {
			return -1;
		}
		return (c1 << 24) | (c2 << 16) | (c3 << 8) | c4;

	}

	static int getLocaleKey(String language_coutry) {
		final int lcLen = language_coutry.length();
		final int key;
		if (lcLen == 2) {
			key = buildKey(language_coutry.charAt(0), language_coutry.charAt(1), (char) 0, (char) 0);
		} else if (lcLen == 5 && isSp(language_coutry.charAt(2)) || lcLen > 5 && isSp(language_coutry.charAt(2)) && isSp(language_coutry.charAt(5))) {
			key = buildKey(language_coutry.charAt(0), language_coutry.charAt(1), language_coutry.charAt(3), language_coutry.charAt(4));
		} else {
			throw unspportedLocale(language_coutry);
		}
		if (key == -1) {
			throw unspportedLocale(language_coutry);
		}
		return key;
	}

	static int getLocaleKey(Locale one) {
		final String lan = one.getLanguage();
		if (lan.length() != 2) {
			throw unspportedLocale(one);
		}
		final String cou = one.getCountry();
		final char c3, c4;
		switch (cou.length()) {
		case 0:
			c3 = 0;
			c4 = 0;
			break;
		case 2:
			c3 = cou.charAt(0);
			c4 = cou.charAt(1);
			break;
		default:
			throw unspportedLocale(one);
		}
		final int key = buildKey(lan.charAt(0), lan.charAt(1), c3, c4);
		if (key == -1) {
			throw unspportedLocale(one);
		}
		return key;
	}

	private final static UnsupportedOperationException unspportedLocale(
			Locale one) {
		return unspportedLocale(one.toString());
	}

	private final static UnsupportedOperationException unspportedLocale(
			String language_coutry) {
		return new UnsupportedOperationException("不支持的地域：".concat(language_coutry));

	}

	private static boolean isSp(char c) {
		return c == '_' || c == '-';
	}
}
