/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File IntIdGenerator.java
 * Date Dec 1, 2009
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public final class IntIdGenerator {
	private int nextId;
	public final int skipId;

	public IntIdGenerator(int skipId) {
		this.skipId = skipId;
		this.nextId = (((int) System.nanoTime()) & 0xFFFFFFFF);
		if (this.nextId == skipId) {
			this.nextId++;
		}
	}

	public final synchronized int next() {
		int next = this.nextId++;
		if (this.nextId == this.skipId) {
			this.nextId++;
		}
		return next;
	}
}
