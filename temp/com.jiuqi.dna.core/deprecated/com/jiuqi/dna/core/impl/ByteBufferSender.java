/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferSender.java
 * Date 2009-6-12
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * FIXME �����֡�
 * 
 * �����ֽڻ������ķ�������
 * 
 * @author LRJ
 * @version 1.0
 */
interface ByteBufferSender {
	/**
	 * ���͸������ֽڻ�������װ�����е����ݡ�
	 * 
	 * @param src
	 *            �ֽڻ�������װ�������з�װ��Ҫ���͵����ݣ���
	 */
	void toSend(ByteBufferWrapper src);
}
