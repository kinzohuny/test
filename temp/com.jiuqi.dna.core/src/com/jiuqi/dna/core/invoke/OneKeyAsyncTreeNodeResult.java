/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File OneKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * ���������ڵ��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public interface OneKeyAsyncTreeNodeResult<TResult, TKey> extends
		AsyncTreeNodeResult<TResult>,
		OneKeyOverlappedTreeNodeResult<TResult, TKey> {
	/**
	 * ��ȡ��ѯ�����еģ���һ��������
	 * 
	 * @return ��ѯ�����еģ���һ��������
	 */
	TKey getKey1();
}
