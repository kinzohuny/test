package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.NSerializer.NSerializerFactory;

/**
 * 序列化器工厂供应商
 * 
 * @author gaojingxin
 * 
 */
public interface NSerializerFactoryProvider {
	/**
	 * 获得序列化器工厂
	 */
	public NSerializerFactory getNSerializerFactory();
}
