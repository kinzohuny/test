/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ThreeKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * �������ڵ��ѯ������Ѿ���������ʹ��ThreeKeyAsyncTreeNodeResult
 * 
 * @author LRJ
 * @version 1.0
 */
@Deprecated
public interface ThreeKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2, TKey3>
		extends TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> {
	/**
	 * ��ȡ��ѯ�����еĵ���������
	 * 
	 * @return ��ѯ�����еĵ���������
	 */
	TKey3 getKey3();
}
