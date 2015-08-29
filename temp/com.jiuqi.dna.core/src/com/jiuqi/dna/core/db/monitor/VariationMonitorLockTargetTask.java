package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * �ڳɹ���������Ŀ����Ļص�����
 * 
 * <p>
 * ����������ȫ�Ķ�ռ����ס�����߼�����������ҵ��æ��ʱʹ�á���������Context�������ͷš�
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
	 *            ָ����Ҫ��ס�ļ���������
	 */
	public VariationMonitorLockTargetTask(GUID monitorId) {
		if (monitorId == null) {
			throw new NullArgumentException("monitor");
		}
		this.monitorId = monitorId;
	}
}