/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTreeNodeQueryStubImpl.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.TreeNode;

/**
 * 远程树结点查询存根的实现。
 * 
 * @author LRJ
 * @version 1.0
 */
final class RemoteTreeNodeQueryStubImpl extends RemoteRequestStubImpl implements
		RemoteTreeNodeQueryStub {

	@SuppressWarnings("unchecked")
	private TreeNode result;

	RemoteTreeNodeQueryStubImpl(NetConnection connection,
			RemoteTreeNodeQuery remoteTreeNodeQuery) {
		super(connection, remoteTreeNodeQuery);
	}

	@SuppressWarnings("unchecked")
	public void setResult(Object result) {
		if (result instanceof TreeNode) {
			this.result = (TreeNode) result;
		} else {
			this.setLocalException(new UnknownObjectException(result == null ? null : result.toString()));
		}
	}

	@SuppressWarnings("unchecked")
	public TreeNode getTreeNode() {
		this.internalCheckException();
		return this.result;
	}
}
