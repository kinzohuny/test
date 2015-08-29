/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File TreeNodeRoot.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.impl;

/**
 * ֻ��Ϊ���ĸ����ʹ�ã���ֻ���˸ýڵ����߼����������еľ��Լ�����Ϣ��
 * 
 * @author LRJ
 * @version 1.0
 */
// TODO �ڹ������Ĵ����У��޸ĸ��ڵ���ʹ�õ��ࡣ
public final class TreeNodeRoot<TData> extends TreeNodeImpl<TData> {

	int absoluteLevel;

	TreeNodeRoot(TData data, int absoluteLevel) {
		super(null, data);
	}

	final int getAbsoluteLevel() {
		return this.absoluteLevel;
	}
}
