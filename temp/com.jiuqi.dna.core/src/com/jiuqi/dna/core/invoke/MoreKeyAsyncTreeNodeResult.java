/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * ������ڵ��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
public interface MoreKeyAsyncTreeNodeResult<TResult, TKey1, TKey2, TKey3>
		extends ThreeKeyAsyncTreeNodeResult<TResult, TKey1, TKey2, TKey3> {
	/**
	 * ��ȡ��ѯ������ǰ������֮�����Щ����
	 * 
	 * @return ��ѯ������ǰ������֮�����Щ����
	 */
	Object[] getOtherKeys();
}
