package com.jiuqi.dna.core.impl;

/**
 * @author Jedicc
 * 
 */
public final class IntBits {

	private int bits;

	public final boolean isEmpty() {
		return this.bits == 0;
	}

	public final void set(int index) {
		checkRange(index);
		this.bits |= 1 << index;
	}

	public final boolean get(int index) {
		checkRange(index);
		return (this.bits & (1 << index)) != 0;
	}

	public final void clear() {
		this.bits = 0;
	}

	public final int cardinality() {
		return Integer.bitCount(this.bits);
	}

	public final int nextSetBit(int fromIndex) {
		checkRange(fromIndex);
		int mask = 0;
		for (int i = fromIndex; i < 32; i++) {
			mask = 1 << i;
			if ((this.bits & mask) > 0) {
				return i;
			}
		}
		return -1;
	}

	public final void or(IntBits bits) {
		this.bits |= bits.bits;
	}

	private static final void checkRange(int index) {
		if (index < 0 || index > 31) {
			throw new IndexOutOfBoundsException();
		}
	}

}
