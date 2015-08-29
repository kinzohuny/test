package com.jiuqi.dna.core.impl;

import java.util.Arrays;

public class BaseDataFragmentImpl {
	protected final byte[] fragment;

	protected int position;

	protected int remain;

	BaseDataFragmentImpl(final int capacity) {
		this.fragment = new byte[capacity];
		this.position = 0;
		this.remain = capacity;
	}

	public void clear() {
		Arrays.fill(this.fragment, 0, this.fragment.length, (byte) 0);
		this.position = 0;
		this.remain = this.fragment.length;
	}
}
