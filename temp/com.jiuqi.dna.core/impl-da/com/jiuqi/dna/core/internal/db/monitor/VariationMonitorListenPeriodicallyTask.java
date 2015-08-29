package com.jiuqi.dna.core.internal.db.monitor;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * ���������Եļ���������
 * 
 * <p>
 * ����ʹ�ô�period��AsyncInfo�����ø����񡣸�����Ĵ�������Ҫ����������ѯ���߼���ͳ����Ϣ�ļ�¼��
 * 
 * @author houchunlei
 * 
 */
final class VariationMonitorListenPeriodicallyTask extends SimpleTask {

	final Timestamp born = new Timestamp(System.currentTimeMillis());

	final String monitor;

	final int tolerance;

	final ArrayList<Throwable> exceptions = new ArrayList<Throwable>();

	VariationMonitorListenPeriodicallyTask(String monitor, int tolerance) {
		this.monitor = monitor;
		this.tolerance = tolerance;
	}
}