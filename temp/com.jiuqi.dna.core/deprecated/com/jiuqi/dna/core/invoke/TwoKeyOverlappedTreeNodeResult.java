/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File TwoKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * ˫�����ڵ��ѯ������Ѿ���������ʹ��TwoKeyAsyncTreeNodeResult
 * 
 * @author LRJ
 * @version 1.0
 */
@Deprecated
public interface TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> extends
		OneKeyOverlappedTreeNodeResult<TResult, TKey1> {

	/**
	 * ��ȡ��ѯ�����еĵڶ�������
	 * 
	 * @return ��ѯ�����еĵڶ�������
	 */
	TKey2 getKey2();
}
