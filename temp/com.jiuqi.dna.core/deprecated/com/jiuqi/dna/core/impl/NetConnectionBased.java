/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ConnectionBased.java
 * Date 2009-3-12
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * �������ӵĶ���ĳ�����ࡣ
 * 
 * @author LRJ
 * @version 1.0
 */
abstract class NetConnectionBased {
	final NetConnection netConnection;

	/**
	 * @param netConnection
	 *            ���Ӷ���
	 * @throws NullArgumentException
	 *             ���ָ�������Ӷ���Ϊ�գ�<code>null</code>����
	 */
	NetConnectionBased(NetConnection netConnection) {
		if (netConnection == null) {
			throw new NullArgumentException("netConnection");
		}
		this.netConnection = netConnection;
	}
}
