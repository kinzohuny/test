package com.jiuqi.dna.core.spi.log;

import com.jiuqi.dna.core.impl.LogTaskInternal;

/**
 * ��־������־������Ҫʵ�ִ��������Ĵ�������ϵͳ�����ʵ���ʱ�����ø�����
 * 
 * @author gaojingxin
 * 
 */
public final class LogTask extends LogTaskInternal {
	public LogTask(Object logManager) {
		super(logManager);
	}

	/**
	 * ��ȡ��һ����־��Ϣֱ������null��ʾȫ�����أ�����־��¼Ӧ����ֹ
	 */
	@Override
	public final LogEntry nextLogEntry() {
		return super.nextLogEntry();
	}
}
