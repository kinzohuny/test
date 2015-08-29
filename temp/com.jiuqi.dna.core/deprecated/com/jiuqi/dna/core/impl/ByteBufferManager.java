/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferManager.java
 * Date 2009-6-12
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * 字节缓冲区管理器。
 * 
 * FIXME 改名字。
 * 
 * @author LRJ
 * @version 1.0
 */
interface ByteBufferManager {
	/**
	 * 获取一个字节缓冲区的包装对象。
	 * 
	 * @return 字节缓冲区的包装对象。
	 */
	ByteBufferWrapper getBuffer();
}
