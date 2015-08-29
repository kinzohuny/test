package com.jiuqi.dna.core.impl;

public class TextBuilder implements CharSequence, Appendable {

	private final StringBuilder s = new StringBuilder();

	public final TextBuilder append(CharSequence csq) {
		this.s.append(csq);
		return this;
	}

	public final TextBuilder append(CharSequence csq, int start, int end) {
		this.s.append(csq, start, end);
		return this;
	}

	public final TextBuilder append(char c) {
		this.s.append(c);
		return this;
	}

	public final int length() {
		return this.s.length();
	}

	public final char charAt(int index) {
		return this.s.charAt(index);
	}

	public final CharSequence subSequence(int start, int end) {
		throw new UnsupportedOperationException();
	}
}
