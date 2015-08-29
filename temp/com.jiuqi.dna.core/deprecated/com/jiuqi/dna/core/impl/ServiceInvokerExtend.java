/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ServiceInvokerExtend.java
 * Date 2009-4-16
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.invoke.MoreKeyAsyncListResult;
import com.jiuqi.dna.core.invoke.MoreKeyAsyncResult;
import com.jiuqi.dna.core.invoke.MoreKeyAsyncTreeNodeResult;
import com.jiuqi.dna.core.service.ServiceInvoker;

/**
 * FIXME 把所有方法整合进ServiceInvoker中。
 * 
 * @author LRJ
 * @version 1.0
 */
@Deprecated
public interface ServiceInvokerExtend extends ServiceInvoker {
	<TResult, TKey1, TKey2, TKey3> MoreKeyAsyncResult<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3,
			Object... otherKeys);

	<TResult, TKey1, TKey2, TKey3> MoreKeyAsyncListResult<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3,
			Object... otherKeys);

	<TFacade, TKey1, TKey2, TKey3> MoreKeyAsyncTreeNodeResult<TFacade, TKey1, TKey2, TKey3> asyncGetTreeNode(
			Class<TFacade> facadeClass, TKey1 key1, TKey2 key2, TKey3 key3,
			Object... otherKeys);
}
