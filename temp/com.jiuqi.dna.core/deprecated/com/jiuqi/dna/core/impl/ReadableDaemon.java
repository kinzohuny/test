/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ReadableDaemon.java
 * Date 2009-2-26
 */
package com.jiuqi.dna.core.impl;

import java.nio.channels.SelectionKey;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * �ɶ�ͨ���ļ໤����<br/>
 * ��������ά��һ���ȴ���ͨ���ж�ȡ���ݵ����Ӷ���Ķ��С���������������ӣ���������е�ͨ��ע�ᵽѡ�����С�
 * ������ͬʱ�����ѡ������ѡ�����ڿɶ�״̬��ͨ������֪ͨ��Ӧ�����Ӷ����Ѿ����Դ�ͨ����ȡ�����ˡ�
 * 
 * @author LRJ
 * @version 1.0
 */
final class ReadableDaemon extends SelectorBased {

	/**
	 * ����ɶ�ͨ���ļ໤����
	 * 
	 * @param connectionManager
	 *            ���ӹ�������
	 * @throws NullArgumentException
	 *             ���ӹ�����Ϊ�ա�
	 * @throws CannotOpenSelectorException
	 *             ѡ����δ�ܿ�����
	 */
	ReadableDaemon(NetManager connectionManager) throws NullArgumentException {
		super(connectionManager, SelectionKey.OP_READ);
	}

	@Override
	final void internalStart() {
		RIUtil.startDaemon(this, "readable-dm");
	}

	@Override
	final void wakeupToWork(NetConnection netConnection) {
		netConnection.wakeupR();
	}
}
