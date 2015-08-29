/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTaskStub.java
 * Date 2009-2-17
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.invoke.Task;

/**
 * Զ����������
 * 
 * @author LRJ
 * @version 1.0
 */
public interface RemoteTaskStub extends RemoteRequestStub {
	/**
	 * ��ȡԶ������ִ����Ϻ󷵻ص��������
	 * 
	 * @return Զ������ִ����Ϻ󷵻ص��������
	 * @throws RemoteException
	 *             Զ�̵��ù����г����쳣��
	 */
	@SuppressWarnings("unchecked")
	Task getReturnedTask() throws RemoteException;
}
