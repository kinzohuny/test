/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteQueryStub.java
 * Date 2009-2-17
 */
package com.jiuqi.dna.core.impl;

/**
 * Զ�̲�ѯ�����
 * 
 * @author LRJ
 * @version 1.0
 */
interface RemoteQueryStub extends RemoteRequestStub {

	/**
	 * ��ȡԶ�̲�ѯִ����Ϻ󷵻صĽ����
	 * 
	 * @return Զ�̲�ѯִ����Ϻ󷵻صĽ����
	 * @throws RemoteException
	 *             Զ�̵��ù����г����쳣��
	 */
	Object getResult() throws RemoteException;

	/**
	 * ���Զ�̲�ѯִ����Ϻ󷵻صĽ���Ƿ�Ϊ�գ�<code>null</code>����
	 * 
	 * @return ���Զ�̲�ѯִ����Ϻ󷵻صĽ��Ϊ�գ���Ϊ<code>true</code>������Ϊ<code>false</code>��
	 * @throws RemoteException
	 *             Զ�̵��ù����г����쳣��
	 */
	boolean isResultNull() throws RemoteException;
}
