/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File MoreKeyOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

/**
 * 多键树节点查询结果。
 * 
 * @author LRJ
 * @version 1.0
 */
public interface MoreKeyAsyncTreeNodeResult<TResult, TKey1, TKey2, TKey3>
		extends ThreeKeyAsyncTreeNodeResult<TResult, TKey1, TKey2, TKey3> {
	/**
	 * 获取查询条件中前三个键之后的那些键。
	 * 
	 * @return 查询条件中前三个键之后的那些键。
	 */
	Object[] getOtherKeys();
}
