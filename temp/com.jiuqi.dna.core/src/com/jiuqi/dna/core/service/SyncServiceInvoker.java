package com.jiuqi.dna.core.service;

import com.jiuqi.dna.core.LifeHandle;
import com.jiuqi.dna.core.ListQuerier;
import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.TreeQuerier;
import com.jiuqi.dna.core.exception.DeadLockException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Task;

public interface SyncServiceInvoker extends ObjectQuerier, ListQuerier,
		TreeQuerier, LifeHandle {
	/**
	 * ��ȡ������������
	 * 
	 * @return ����0�������̵��ã�0��1֮������ص��ã�1�Լ�1���ϴ���Զ�̵���
	 */
	public float getResistance();
	
	// /////////////////////////////////////////
	// /////// ��������
	// /////////////////////////////////////////
	public <TMethod extends Enum<TMethod>> void handle(Task<TMethod> task,
			TMethod method) throws DeadLockException;

	public void handle(SimpleTask task) throws DeadLockException;
}
