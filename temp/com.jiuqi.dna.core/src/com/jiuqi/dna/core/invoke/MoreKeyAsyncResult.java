/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedResult.java
 * Date 2009-4-8
 */
package com.jiuqi.dna.core.invoke;

/**
 * �����ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
public interface MoreKeyAsyncResult<TResult, TKey1, TKey2, TKey3> extends
		ThreeKeyAsyncResult<TResult, TKey1, TKey2, TKey3> {
	/**
	 * ��ȡ��ѯ������ǰ������֮�����Щ����
	 * 
	 * @return ��ѯ������ǰ������֮�����Щ����
	 */
	Object[] getOtherKeys();
}
