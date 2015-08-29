/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTreeNodeQueryStub.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.TreeNode;

/**
 * Զ�����ڵ��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
interface RemoteTreeNodeQueryStub extends RemoteRequestStub {
	/**
	 * ��ȡԶ�̲�ѯ�����󷵻ص����ڵ㡣
	 * 
	 * @return Զ�̲�ѯ�����󷵻ص����ڵ㡣
	 */
	@SuppressWarnings("unchecked")
	TreeNode getTreeNode();
}
