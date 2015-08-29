/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File AsyncTreeNodeResult.java
 * Date 2009-4-9
 */
package com.jiuqi.dna.core.invoke;

import com.jiuqi.dna.core.TreeNode;

/**
 * �첽�����ڵ��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
public interface AsyncTreeNodeResult<TFacade> extends AsyncHandle {

	/**
	 * ��ȡִ�������ȡ�õĽ�����ý����һ�����ڵ㣬�����������νṹ�ĸ��ڵ㡣
	 * 
	 * @return ���ڵ����
	 * @throws IllegalStateException
	 *             ��������δ���أ����׳����쳣��
	 */
	TreeNode<TFacade> getResultTreeNode() throws IllegalStateException;

	/**
	 * ��ȡ���������Ӧ���ݵ�������͡�
	 * 
	 * @return �������ʵ����
	 */
	Class<TFacade> getResultClass();
}
