/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File OneKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * ���������ڵ��ѯ������Ѿ���������ʹ��OneKeyAsyncTreeNodeResult
 * 
 * @author LRJ
 * @version 1.0
 */
@Deprecated
public interface OneKeyOverlappedTreeNodeResult<TResult, TKey> extends
		AsyncTreeNodeResult<TResult> {
	/**
	 * ��ȡ��ѯ�����еģ���һ��������
	 * 
	 * @return ��ѯ�����еģ���һ��������
	 */
	TKey getKey1();
}
