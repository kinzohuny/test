/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File OneKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * 单键的树节点查询结果。
 * 
 * @author LRJ
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public interface OneKeyAsyncTreeNodeResult<TResult, TKey> extends
		AsyncTreeNodeResult<TResult>,
		OneKeyOverlappedTreeNodeResult<TResult, TKey> {
	/**
	 * 获取查询条件中的（第一个）键。
	 * 
	 * @return 查询条件中的（第一个）键。
	 */
	TKey getKey1();
}
