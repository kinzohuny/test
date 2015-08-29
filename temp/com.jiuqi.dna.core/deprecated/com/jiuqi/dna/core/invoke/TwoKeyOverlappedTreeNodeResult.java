/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File TwoKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * 双键树节点查询结果，已经废弃，请使用TwoKeyAsyncTreeNodeResult
 * 
 * @author LRJ
 * @version 1.0
 */
@Deprecated
public interface TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> extends
		OneKeyOverlappedTreeNodeResult<TResult, TKey1> {

	/**
	 * 获取查询条件中的第二个键。
	 * 
	 * @return 查询条件中的第二个键。
	 */
	TKey2 getKey2();
}
