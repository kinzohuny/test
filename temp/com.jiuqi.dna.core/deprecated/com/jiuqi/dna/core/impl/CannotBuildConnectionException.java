/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File CannotBuildConnectionException.java
 * Date 2009-4-14
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public class CannotBuildConnectionException extends RuntimeException {
	private static final long serialVersionUID = -1840610215641733547L;

	public CannotBuildConnectionException() {
		super("无法建立连接");
	}

	public CannotBuildConnectionException(Throwable cause) {
		super("无法建立连接", cause);
	}

}
