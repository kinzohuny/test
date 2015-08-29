/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ConnectionManagerBased.java
 * Date 2009-2-26
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructField;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * ��������������Ķ���ĳ�����ࡣ
 * 
 * @author LRJ
 * @version 1.0
 */
abstract class NetManagerBased {
	@StructField(stateField = false)
	final transient NetManager netManager;

	/**
	 * ��ָ�������ӹ������������Ķ���
	 * 
	 * @param netManager
	 *            �����������
	 * @throws NullArgumentException
	 *             ���ṩ�����������Ϊ�գ�<code>null</code>��ʱ��
	 */
	NetManagerBased(NetManager netManager) {
		if (netManager == null) {
			throw new NullArgumentException("netManager");
		}
		this.netManager = netManager;
	}
}
