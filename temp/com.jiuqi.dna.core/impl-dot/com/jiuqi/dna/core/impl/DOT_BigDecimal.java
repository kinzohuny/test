package com.jiuqi.dna.core.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.jiuqi.dna.core.type.DataObjectTranslator;

final class DOT_BigDecimal implements DataObjectTranslator<BigDecimal, byte[]> {

	private static final short VERSION = 0x0100;

	public final void recoverData(final BigDecimal dest, final byte[] delegate,
			short version, boolean forSerial) {
		// do nothing
	}

	public final BigDecimal resolveInstance(final BigDecimal destHint,
			final byte[] delegate, short version, boolean forSerial) {
		final byte[] bigIntegerByteArray = new byte[delegate.length - 1];
		System.arraycopy(delegate, 1, bigIntegerByteArray, 0, bigIntegerByteArray.length);
		final BigInteger bigInteger = new BigInteger(bigIntegerByteArray);
		final int scale = delegate[0];
		final BigDecimal bigDecimal = new BigDecimal(bigInteger, scale);
		if (bigDecimal.equals(destHint)) {
			return destHint;
		} else {
			return bigDecimal;
		}
	}

	public final byte[] toDelegateObject(final BigDecimal hintValue) {
		final BigInteger unscaledValue = hintValue.unscaledValue();
		final int scale = hintValue.scale();
		if (scale > Byte.MAX_VALUE || scale < Byte.MIN_VALUE) {
			throw new IllegalStateException("不支持BigDecimal中scale取值超出Byte范围的序列化");
		}
		final byte[] unscaledValueByteArray = unscaledValue.toByteArray();
		final int length = unscaledValueByteArray.length;
		final byte[] delegate = new byte[length + 1];
		delegate[0] = (byte) scale;
		System.arraycopy(unscaledValueByteArray, 0, delegate, 1, length);
		return delegate;
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
