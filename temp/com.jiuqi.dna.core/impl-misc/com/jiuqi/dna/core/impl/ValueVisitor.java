/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ValueVisitor.java
 * Date 2009-5-25
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public interface ValueVisitor<TValue> {

	void visit(int key, TValue value);
}
