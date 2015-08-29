/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ResourceTreeEntry_Info.java
 * Date May 6, 2009
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
final class ClusterResInfo_TreeEntry extends AbstractClusterResInfo {
	final long parentItemId;

	// REMIND? ����ÿ������л�
	final Object treeId;

	ClusterResInfo_TreeEntry(Object categoryOrId, Class<?> facadeClass,
			Object treeId, long parentItemId, long childItemId, Action action) {
		super(categoryOrId, facadeClass, childItemId);
		if (treeId == null) {
			treeId = None.NONE;
		}
		if (action == null) {
			throw new NullArgumentException("action");
		}
		this.treeId = treeId;
		this.parentItemId = parentItemId;
		this.action = action;
	}

	final Action action;

	static enum Action {
		INIT, ADD, MOVE, DELETE
	}

	@Override
	void exec(ContextImpl<?, ?, ?> context) throws Throwable {
		// TODO Auto-generated method stub
	}
}
