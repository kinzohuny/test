package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.NSerializer.NSerializerFactory;

/**
 * ���л���������Ӧ��
 * 
 * @author gaojingxin
 * 
 */
public interface NSerializerFactoryProvider {
	/**
	 * ������л�������
	 */
	public NSerializerFactory getNSerializerFactory();
}
