package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * 在成功锁定监视目标表后的回调任务
 * 
 * <p>
 * 该锁定会完全的独占的锁住整张逻辑表。不建议在业务繁忙的时使用。锁定会在Context结束后释放。
 * 
 * @author houchunlei
 * 
 * @see VariationMonitorTargetBusyException
 * 
 */
public final class VariationMonitorLockTargetTask extends SimpleTask {

	public final GUID monitorId;

	/**
	 * @param monitor
	 *            指定需要锁住的监视器名称
	 */
	public VariationMonitorLockTargetTask(GUID monitorId) {
		if (monitorId == null) {
			throw new NullArgumentException("monitor");
		}
		this.monitorId = monitorId;
	}
}