/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ByteBufferManager.java
 * Date 2009-6-12
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.ByteBufferPool.ByteBufferWrapper;

/**
 * �ֽڻ�������������
 * 
 * FIXME �����֡�
 * 
 * @author LRJ
 * @version 1.0
 */
interface ByteBufferManager {
	/**
	 * ��ȡһ���ֽڻ������İ�װ����
	 * 
	 * @return �ֽڻ������İ�װ����
	 */
	ByteBufferWrapper getBuffer();
}
