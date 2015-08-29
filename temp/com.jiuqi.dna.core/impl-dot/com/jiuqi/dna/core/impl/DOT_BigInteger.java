package com.jiuqi.dna.core.impl;

import java.math.BigInteger;

import com.jiuqi.dna.core.type.DataObjectTranslator;

final class DOT_BigInteger implements DataObjectTranslator<BigInteger, byte[]> {

	private static final short VERSION = 0x0100;

	public void recoverData(final BigInteger dest, final byte[] delegate,
			short version, boolean forSerial) {
		// do nothing
	}

	public BigInteger resolveInstance(final BigInteger destHint,
			final byte[] delegate, short version, boolean forSerial) {
		final BigInteger bigInteger = new BigInteger(delegate);
		if (bigInteger.equals(destHint)) {
			return destHint;
		} else {
			return bigInteger;
		}
	}

	public byte[] toDelegateObject(final BigInteger hintValue) {
		return hintValue.toByteArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public boolean supportAssign() {
		return false;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
