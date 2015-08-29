/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ThreeKeyQueryBy.java
 * Date 2009-4-8
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * ������ѯƾ�ݡ�
 * 
 * @author LRJ
 * @version 1.0
 */
@StructClass
class ThreeKeyQueryBy extends TwoKeyQueryBy {

	/**
	 * ��ѯƾ���еĵ���������
	 */
	final Object key3;

	/**
	 * ������ѯƾ�ݵĹ�������
	 * 
	 * @param resultClass
	 *            ��ѯ��������͡�
	 * @param key1
	 *            ��ѯƾ�ݵĵ�һ������
	 * @param key2
	 *            ��ѯƾ�ݵĵڶ�������
	 * @param key3
	 *            ��ѯƾ�ݵĵ���������
	 */
	ThreeKeyQueryBy(Class<?> resultClass, Object key1, Object key2, Object key3) {
		super(resultClass, key1, key2);
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.key3 = key3;
	}

	@Override
	public String toString() {
		return "{" + this.resultClass.getName() + "(" + this.key + "," + this.key2 + "," + this.key3 + ")}";
	}

	/**
	 * ��ȡ��ѯƾ���еĵ���������
	 * 
	 * @return ��ѯƾ���еĵ���������
	 */
	@Override
	final Object getKey3() {
		return this.key3;
	}
}
