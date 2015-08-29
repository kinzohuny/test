/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ThreeKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * �������ڵ��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public interface ThreeKeyAsyncTreeNodeResult<TResult, TKey1, TKey2, TKey3>
		extends TwoKeyAsyncTreeNodeResult<TResult, TKey1, TKey2>,
		ThreeKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2, TKey3> {
	/**
	 * ��ȡ��ѯ�����еĵ���������
	 * 
	 * @return ��ѯ�����еĵ���������
	 */
	TKey3 getKey3();
}
