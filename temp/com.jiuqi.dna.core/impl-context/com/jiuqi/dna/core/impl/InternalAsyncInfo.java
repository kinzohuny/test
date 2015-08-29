package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.service.AsyncInfo.AwaitSchedule;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;

/**
 * �첽������Ϣ
 * 
 * @author gaojingxin
 * 
 */
public abstract class InternalAsyncInfo {

	/**
	 * ��ʼ��ʱ��
	 */
	protected long start;

	protected AwaitSchedule awaitSchedule;

	/**
	 * �ظ�ִ�е�����С�ڻ����0��ʾ���ظ�
	 */
	protected long period;

	protected SessionMode sessionMode;

	/**
	 * �����첽���õ��в�������Ϣ
	 */
	protected boolean careInfos;

	public InternalAsyncInfo(long start, AwaitSchedule awaitSchedule,
			long period, SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		this.start = start;
		this.awaitSchedule = awaitSchedule;
		this.period = period;
		this.sessionMode = sessionMode;
	}
}