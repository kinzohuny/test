/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteEventStubImpl.java
 * Date 2009-4-16
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
final class RemoteEventStubImpl extends RemoteRequestStubImpl implements
		RemoteEventStub {
	RemoteEventStubImpl(NetConnection connection, RemoteEvent remoteEvent) {
		super(connection, remoteEvent);
	}

	public void setResult(Object result) {
		// 没有任何数据需要返回
	}
}
