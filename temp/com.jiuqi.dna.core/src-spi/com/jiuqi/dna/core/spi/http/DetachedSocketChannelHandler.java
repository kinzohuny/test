/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File DetachedSocketChannelHandler.java
 * Date 2009-10-23
 */
package com.jiuqi.dna.core.spi.http;

import java.nio.channels.SocketChannel;

/**
 * A handler for the detached <code>java.nio.channels.SocketChannel</code> from
 * Jetty.
 * 
 * The handler will receive a <code>SocketChannel</code> object which was
 * detached from Jetty, and do other works on it.
 * 
 * @author LRJ
 * @version 1.0
 */
public interface DetachedSocketChannelHandler {
	/**
	 * Receive a <code>SocketChannel</code> object detached from Jetty, which is
	 * in blocking mode.
	 * 
	 * @param socketChannel
	 *            the channel detached from Jetty and not <code>null</code>.
	 */
	void handleDetackedSocketChannel(SocketChannel socketChannel);
}
