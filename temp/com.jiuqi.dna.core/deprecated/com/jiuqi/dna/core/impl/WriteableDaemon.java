/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File WriteDaemon.java
 * Date 2009-2-20
 */
package com.jiuqi.dna.core.impl;

import java.nio.channels.SelectionKey;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * ��дͨ���ļ໤����<br/>
 * ��������ά��һ���ȴ���ͨ����д�����ݵ����Ӷ���Ķ��С���������������ӣ���������е�ͨ��ע�ᵽѡ�����С�
 * ������ͬʱ�����ѡ������ѡ�����ڿ�д״̬��ͨ������֪ͨ��Ӧ�����Ӷ����Ѿ�������ͨ��д�������ˡ�
 * 
 * @author LRJ
 * @version 1.0
 */
final class WriteableDaemon extends SelectorBased implements Runnable {

	/**
	 * �����дͨ���ļ໤����
	 * 
	 * @param connectionManager
	 *            ���ӹ�������
	 * @throws NullArgumentException
	 *             ���ӹ�����Ϊ�ա�
	 * @throws CannotOpenSelectorException
	 *             ѡ����δ�ܿ�����
	 */
	WriteableDaemon(NetManager connectionManager) throws NullArgumentException {
		super(connectionManager, SelectionKey.OP_WRITE);
	}

	@Override
	final void internalStart() {
		RIUtil.startDaemon(this, "writable-dm");
	}

	@Override
	final void wakeupToWork(NetConnection netConnection) {
		netConnection.wakeupW();
	}
}
