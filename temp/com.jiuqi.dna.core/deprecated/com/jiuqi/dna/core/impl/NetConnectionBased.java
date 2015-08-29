/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ConnectionBased.java
 * Date 2009-3-12
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 基于连接的对象的抽象基类。
 * 
 * @author LRJ
 * @version 1.0
 */
abstract class NetConnectionBased {
	final NetConnection netConnection;

	/**
	 * @param netConnection
	 *            连接对象。
	 * @throws NullArgumentException
	 *             如果指定的连接对象为空（<code>null</code>）。
	 */
	NetConnectionBased(NetConnection netConnection) {
		if (netConnection == null) {
			throw new NullArgumentException("netConnection");
		}
		this.netConnection = netConnection;
	}
}
