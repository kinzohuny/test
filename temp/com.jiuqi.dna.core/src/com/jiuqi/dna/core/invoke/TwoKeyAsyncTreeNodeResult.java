/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File TwoKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * ˫�����ڵ��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public interface TwoKeyAsyncTreeNodeResult<TResult, TKey1, TKey2> extends
		OneKeyAsyncTreeNodeResult<TResult, TKey1>,
		TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> {
	/**
	 * ��ȡ��ѯ�����еĵڶ�������
	 * 
	 * @return ��ѯ�����еĵڶ�������
	 */
	TKey2 getKey2();
}
