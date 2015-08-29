/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferSender.java
 * Date 2009-6-12
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * FIXME 改名字。
 * 
 * 基于字节缓冲区的发送器。
 * 
 * @author LRJ
 * @version 1.0
 */
interface ByteBufferSender {
	/**
	 * 发送给定的字节缓冲区包装对象中的数据。
	 * 
	 * @param src
	 *            字节缓冲区包装对象（其中封装需要发送的数据）。
	 */
	void toSend(ByteBufferWrapper src);
}
