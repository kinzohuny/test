package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

public class LanguagePackage {
	final int localeKey;
	final String[] nameMessages;
	LanguagePackage next;

	LanguagePackage(int localeKey, String[] nameValues) {
		if (nameValues == null || nameValues.length == 0) {
			throw new NullArgumentException("nameValues");
		}
		this.localeKey = localeKey;
		this.nameMessages = nameValues;
	}
}
