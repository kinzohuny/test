/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteRequestStub.java
 * Date 2009-2-17
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public interface RemoteRequestStub {

	int id();

	PacketCode requestPacketCode();

	Throwable getException();

	boolean noException();
}
