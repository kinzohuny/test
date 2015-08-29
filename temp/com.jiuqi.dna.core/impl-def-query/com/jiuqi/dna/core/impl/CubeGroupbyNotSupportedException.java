package com.jiuqi.dna.core.impl;

/**
 * Cube类型的汇总已经停止支持。
 * 
 * @author houchunlei
 * 
 */
public final class CubeGroupbyNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 7685169230624116812L;

	public CubeGroupbyNotSupportedException() {
		super(message());
	}

	public static final String message() {
		return "Cube类型的汇总已经停止支持。";
	}
}
