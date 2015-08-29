/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedResultList.java
 * Date 2009-4-8
 */
package com.jiuqi.dna.core.invoke;

/**
 * �����ѯ����б�
 * 
 * @author LRJ
 * @version 1.0
 */
public interface MoreKeyAsyncListResult<TResult, TKey1, TKey2, TKey3> extends
		ThreeKeyAsyncListResult<TResult, TKey1, TKey2, TKey3> {
	/**
	 * ��ȡ��ѯ������ǰ������֮�����Щ����
	 * 
	 * @return ��ѯ������ǰ������֮�����Щ����
	 */
	Object[] getOtherKeys();
}
