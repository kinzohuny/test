/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File TwoKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * 双键树节点查询结果。
 * 
 * @author LRJ
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public interface TwoKeyAsyncTreeNodeResult<TResult, TKey1, TKey2> extends
		OneKeyAsyncTreeNodeResult<TResult, TKey1>,
		TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> {
	/**
	 * 获取查询条件中的第二个键。
	 * 
	 * @return 查询条件中的第二个键。
	 */
	TKey2 getKey2();
}
