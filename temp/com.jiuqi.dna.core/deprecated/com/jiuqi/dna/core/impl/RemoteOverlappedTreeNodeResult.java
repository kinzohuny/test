/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteOverlappedTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.MoreKeyAsyncTreeNodeResult;

/**
 * Զ�����ڵ��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
final class RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3>
		extends RemoteAsyncHandle implements
		MoreKeyAsyncTreeNodeResult<TFacade, TKey1, TKey2, TKey3> {

	/**
	 * Զ�����ڵ��ѯ��
	 */
	final RemoteTreeNodeQuery remoteTreeNodeQuery;

	/**
	 * Զ�����ڵ��ѯ����Ĺ�������
	 * 
	 * @param remoteTreeNodeQuery
	 *            Զ�����ڵ��ѯ��
	 * @param remoteTreeNodeQueryStub
	 *            Զ�����ڵ��ѯ�����
	 */
	RemoteOverlappedTreeNodeResult(RemoteTreeNodeQuery remoteTreeNodeQuery,
			RemoteTreeNodeQueryStubImpl remoteTreeNodeQueryStub) {
		super(remoteTreeNodeQueryStub);
		if (remoteTreeNodeQuery == null) {
			throw new NullArgumentException("remoteTreeNodeQuery");
		}
		this.remoteTreeNodeQuery = remoteTreeNodeQuery;
	}

	/**
	 * ��ȡԶ�����ڵ��ѯ�����
	 * 
	 * @return Զ�����ڵ��ѯ�����
	 */
	private RemoteTreeNodeQueryStubImpl remoteTreeNodeQueryStub() {
		return (RemoteTreeNodeQueryStubImpl) this.remoteRequestStub;
	}

	/**
	 * ��ȡ��ѯƾ���еĵ�һ������
	 * 
	 * @return ��ѯƾ���еĵ�һ������
	 */
	@SuppressWarnings("unchecked")
	public TKey1 getKey1() {
		return (TKey1) this.remoteTreeNodeQuery.queryBy.getKey1();
	}

	/**
	 * ��ȡ��ѯƾ���еĵڶ�������
	 * 
	 * @return ��ѯƾ���еĵڶ�������
	 */
	@SuppressWarnings("unchecked")
	public TKey2 getKey2() {
		return (TKey2) this.remoteTreeNodeQuery.queryBy.getKey2();
	}

	/**
	 * ��ȡ��ѯƾ���еĵ���������
	 * 
	 * @return ��ѯƾ���еĵ���������
	 */
	@SuppressWarnings("unchecked")
	public TKey3 getKey3() {
		return (TKey3) this.remoteTreeNodeQuery.queryBy.getKey3();
	}

	/**
	 * ��ȡ��ѯƾ����ǰ������֮��ļ���
	 * 
	 * @return ��ѯƾ����ǰ������֮��ļ���
	 */
	public Object[] getOtherKeys() {
		return this.remoteTreeNodeQuery.queryBy.getOtherKeys();
	}

	/**
	 * ��ȡ���ڵ��е����ݵ����͡�
	 * 
	 * @return ���ڵ��е����ݵ����͡�
	 */
	@SuppressWarnings("unchecked")
	public Class<TFacade> getResultClass() {
		return this.remoteTreeNodeQuery.queryBy.getResultClass();
	}

	/**
	 * ��ȡԶ�̲�ѯ�����󷵻ص����ڵ�����
	 * 
	 * @return Զ�̲�ѯ�����󷵻ص����ڵ�����
	 * @throws IllegalStateException
	 *             ִ�й�����δ��ϡ�
	 * @throws RemoteException
	 *             Զ�̵��ù����г����쳣��
	 */
	@SuppressWarnings("unchecked")
	public TreeNode<TFacade> getResultTreeNode() throws IllegalStateException {
		this.internalCheckStateForResultOK();
		return this.remoteTreeNodeQueryStub().getTreeNode();
	}
}