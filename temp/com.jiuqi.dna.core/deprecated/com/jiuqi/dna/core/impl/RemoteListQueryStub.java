/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteListQueryStub.java
 * Date 2009-4-8
 */
package com.jiuqi.dna.core.impl;

import java.util.List;

/**
 * Զ�̽���б��ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
interface RemoteListQueryStub extends RemoteRequestStub {

	/**
	 * ��ȡԶ�̲�ѯ�����󷵻صĽ���б�
	 * 
	 * @return Զ�̲�ѯ�����󷵻صĽ���б�
	 * @throws RemoteException
	 *             Զ��ִ�й����г����쳣��
	 */
	@SuppressWarnings("unchecked")
	List getResultList() throws RemoteException;
}
