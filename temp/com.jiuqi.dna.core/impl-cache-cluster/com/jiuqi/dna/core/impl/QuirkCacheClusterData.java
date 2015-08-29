package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

public final class QuirkCacheClusterData {

	private GUID id;

	private int index;

	private int from;

	private long time;

	private byte[] data;

	public GUID getId() {
		return this.id;
	}

	public void setId(GUID id) {
		this.id = id;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getTime() {
		return this.time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getFrom() {
		return this.from;
	}

	public void setFrom(int from) {
		this.from = from;
	}
}