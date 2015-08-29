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
	 * 获取调用器的阻力
	 * 
	 * @return 返回0代表本进程调用，0和1之间代表本地调用，1以及1以上代表远程调用
	 */
	public float getResistance();
	
	// /////////////////////////////////////////
	// /////// 处理任务
	// /////////////////////////////////////////
	public <TMethod extends Enum<TMethod>> void handle(Task<TMethod> task,
			TMethod method) throws DeadLockException;

	public void handle(SimpleTask task) throws DeadLockException;
}
