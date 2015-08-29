/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File UnknownObjectException.java
 * Date 2009-4-14
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public class UnknownObjectException extends RuntimeException {
	private static final long serialVersionUID = -8268452639501643928L;

	UnknownObjectException(String message) {
		super(message);
	}
}
