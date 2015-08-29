/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ThreeKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * 三键树节点查询结果，已经废弃，请使用ThreeKeyAsyncTreeNodeResult
 * 
 * @author LRJ
 * @version 1.0
 */
@Deprecated
public interface ThreeKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2, TKey3>
		extends TwoKeyOverlappedTreeNodeResult<TResult, TKey1, TKey2> {
	/**
	 * 获取查询条件中的第三个键。
	 * 
	 * @return 查询条件中的第三个键。
	 */
	TKey3 getKey3();
}
