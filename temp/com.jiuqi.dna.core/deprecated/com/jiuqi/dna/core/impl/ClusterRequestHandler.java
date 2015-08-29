/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ClusterRequestHandler.java
 * Date 2009-5-25
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.spi.work.Work;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
final class ClusterRequestHandler extends Work {
	final NetConnection connection;
	final int id;

	ClusterRequestHandler(NetConnection netConnection, int requestId) {
		if (netConnection == null) {
			throw new NullArgumentException("netConnection");
		}
		this.connection = netConnection;
		this.id = requestId;
	}
}
