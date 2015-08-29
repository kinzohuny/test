package com.jiuqi.dna.core.impl;

public class DOT_StringBuffer extends DOT_StringBuilderBase<StringBuffer> {

	public void recoverData(final StringBuffer dest, final char[] delegate,
			short version, boolean forSerial) {
		dest.append(delegate);
	}

	public StringBuffer resolveInstance(StringBuffer destHint,
			final char[] delegate, short version, boolean forSerial) {
		if (destHint == null) {
			destHint = new StringBuffer(delegate.length);
		} else {
			// StringBuffer«Âø’÷ÿ”√
			destHint.setLength(0);
		}
		return destHint;
	}

	public char[] toDelegateObject(final StringBuffer hintValue) {
		final int length = hintValue.length();
		final char[] delegate = new char[length];
		hintValue.getChars(0, length, delegate, 0);
		return delegate;
	}

}
