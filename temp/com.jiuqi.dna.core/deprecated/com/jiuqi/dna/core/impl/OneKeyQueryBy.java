/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File OneKeyQueryBy.java
 * Date 2009-4-8
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 单键查询凭据。
 * 
 * @author LRJ
 * @version 1.0
 */
@StructClass
class OneKeyQueryBy extends QueryBy {
	/**
	 * 查询凭据中的（第一个）键。
	 */
	final Object key;

	/**
	 * 单键查询凭据构造器。
	 * 
	 * @param resultClass
	 *            查询结果的类型。
	 * @param key
	 *            查询凭据中的（第一个）键值。
	 */
	OneKeyQueryBy(Class<?> resultClass, Object key) {
		super(resultClass);
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.key = key;
	}

	@Override
	public String toString() {
		return "{" + this.resultClass.getName() + "(" + this.key + ")}";
	}

	/**
	 * 获取查询凭据中的（第一个）键。
	 * 
	 * @return 查询凭据中的（第一个）键。
	 */
	@Override
	final Object getKey1() {
		return this.key;
	}
}
