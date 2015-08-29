package com.jiuqi.dna.core.impl;

public class DOT_StringBuilder extends DOT_StringBuilderBase<StringBuilder> {

	public void recoverData(final StringBuilder dest, final char[] delegate,
			short version, boolean forSerial) {
		dest.append(delegate);
	}

	public StringBuilder resolveInstance(StringBuilder destHint,
			final char[] delegate, short version, boolean forSerial) {
		if (destHint == null) {
			destHint = new StringBuilder(delegate.length);
		} else {
			// StringBuilder«Âø’÷ÿ”√
			destHint.setLength(0);
		}
		return destHint;
	}

	public char[] toDelegateObject(final StringBuilder hintValue) {
		final int length = hintValue.length();
		final char[] delegate = new char[length];
		hintValue.getChars(0, length, delegate, 0);
		return delegate;
	}

}
