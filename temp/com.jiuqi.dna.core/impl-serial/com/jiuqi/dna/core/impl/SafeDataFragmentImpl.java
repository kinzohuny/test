package com.jiuqi.dna.core.impl;

final class SafeDataFragmentImpl extends BaseDataFragmentImpl implements
		DataFragment {

	/**
	 * 构造数据段
	 * 
	 * @param capacity
	 *            数据段长度，不能为负数
	 */
	SafeDataFragmentImpl(final int capacity) {
		super(capacity);
	}

	public final int getAvailableOffset() {
		return 0;
	}

	public final int getAvailableLength() {
		return this.fragment.length;
	}

	public final byte[] getBytes() {
		return this.fragment;
	}

	public final int getPosition() {
		return this.position;
	}

	public final void setPosition(final int position) {
		this.seek(position);
	}

	public final int remain() {
		return this.remain;
	}

	public final void limit(final int position) {
		final int oldPosition = this.position;
		if (position < oldPosition || this.getAvailableOffset() + this.getAvailableLength() < position) {
			throw new IllegalArgumentException("position");
		}
		this.remain = position - oldPosition;
	}

	public final byte readByte() {
		final byte result = this.fragment[this.position++];
		this.remain -= 1;
		return result;
	}

	public final short readShort() {
		final byte[] fragment = this.fragment;
		int position = this.position;
		short result = (short) (fragment[position++] & 0xFF);
		result = (short) (result | (fragment[position++] << 8));
		this.position = position;
		this.remain -= 2;
		return result;
	}

	public final char readChar() {
		final byte[] fragment = this.fragment;
		int position = this.position;
		char result = (char) (fragment[position++] & 0xFF);
		result = (char) (result | (fragment[position++] << 8));
		this.position = position;
		this.remain -= 2;
		return result;
	}

	public final int readInt() {
		final byte[] fragment = this.fragment;
		int position = this.position;
		int result = fragment[position++] & 0xFF;
		result |= ((fragment[position++] & 0xFF) << 8);
		result |= ((fragment[position++] & 0xFF) << 16);
		result |= (fragment[position++] << 24);
		this.position = position;
		this.remain -= 4;
		return result;
	}

	public final float readFloat() {
		return Float.intBitsToFloat(this.readInt());
	}

	public final long readLong() {
		final byte[] fragment = this.fragment;
		int position = this.position;
		long result = ((long) fragment[position++]) & 0xFF;
		result |= ((((long) fragment[position++]) & 0xFF) << 8);
		result |= ((((long) fragment[position++]) & 0xFF) << 16);
		result |= ((((long) fragment[position++]) & 0xFF) << 24);
		result |= ((((long) fragment[position++]) & 0xFF) << 32);
		result |= ((((long) fragment[position++]) & 0xFF) << 40);
		result |= ((((long) fragment[position++]) & 0xFF) << 48);
		result |= (((long) fragment[position++]) << 56);
		this.position = position;
		this.remain -= 8;
		return result;
	}

	public final double readDouble() {
		return Double.longBitsToDouble(this.readLong());
	}

	public final void writeByte(final byte value) {
		this.fragment[this.position++] = value;
		this.remain -= 1;
	}

	public final void writeShort(final short value) {
		final byte[] fragment = this.fragment;
		int position = this.position;
		fragment[position++] = (byte) (value & 0xFF);
		fragment[position++] = (byte) ((value >>> 8) & 0xFF);
		this.position = position;
		this.remain -= 2;
	}

	public final void writeChar(final char value) {
		final byte[] fragment = this.fragment;
		int position = this.position;
		fragment[position++] = (byte) (value & 0xFF);
		fragment[position++] = (byte) ((value >>> 8) & 0xFF);
		this.position = position;
		this.remain -= 2;
	}

	public final void writeInt(final int value) {
		final byte[] fragment = this.fragment;
		int position = this.position;
		int v = value;
		fragment[position++] = (byte) (v & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>> 8) & 0xFF);
		this.position = position;
		this.remain -= 4;
	}

	public final void writeFloat(final float value) {
		this.writeInt(Float.floatToRawIntBits(value));
	}

	public final void writeLong(final long value) {
		final byte[] fragment = this.fragment;
		int position = this.position;
		long v = value;
		fragment[position++] = (byte) (v & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>>= 8) & 0xFF);
		fragment[position++] = (byte) ((v >>> 8) & 0xFF);
		this.position = position;
		this.remain -= 8;
	}

	public final void writeDouble(final double value) {
		this.writeLong(Double.doubleToRawLongBits(value));
	}

	public final int skip(final int n) {
		return n == 0 ? this.position : this.seek(this.position + n);
	}

	private final int seek(final int position) {
		final int oldPosition = this.position;
		final int oldRemain = this.remain;
		if (position < this.getAvailableOffset() || oldPosition + oldRemain < position) {
			throwIndexOutOfBoundsException(position, this.getAvailableOffset(), oldPosition + oldRemain - 1);
		}
		this.position = position;
		this.remain += oldPosition - position;
		return oldPosition;
	}

	private static final void throwIndexOutOfBoundsException(final int index,
			final int startIndex, final int endIndex) {
		throw new IndexOutOfBoundsException(index + "/[" + startIndex + ".." + endIndex + "]");
	}
}
