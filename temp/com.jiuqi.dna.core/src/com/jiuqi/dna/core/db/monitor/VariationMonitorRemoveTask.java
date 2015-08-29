package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * ɾ��������
 * 
 * @author houchunlei
 * 
 */
public final class VariationMonitorRemoveTask extends SimpleTask {

	public final GUID monitorId;

	public VariationMonitorRemoveTask(GUID monitorId) {
		this.monitorId = monitorId;
	}
}