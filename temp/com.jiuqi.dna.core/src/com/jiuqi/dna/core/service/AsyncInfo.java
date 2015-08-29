package com.jiuqi.dna.core.service;

import java.util.Date;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.InternalAsyncInfo;

/**
 * �첽������Ϣ
 * 
 * @author gaojingxin
 * 
 */
public final class AsyncInfo extends InternalAsyncInfo {

	/**
	 * �Ựģʽ
	 * 
	 * @author gaojingxin
	 * 
	 */
	public enum SessionMode {

		/**
		 * ��ͬ�ĻỰ�У�
		 */
		SAME,

		/**
		 * ��ͬ�ĻỰ����ͬ���û�������ϵͳ�Ự�·�����첽�������ʹ�������û�
		 */
		INDIVIDUAL,

		/**
		 * ��ͬ�ĻỰ�������û�
		 */
		INDIVIDUAL_ANONYMOUS
	}

	/**
	 * ���ȵȴ�
	 * 
	 * <p>
	 * ָʾ��ָ�����¼��������첽����ŻῪʼ���ȡ�
	 * 
	 * @author houchunlei
	 * 
	 */
	public enum AwaitSchedule {

		/**
		 * ���ȴ���������ʼ����
		 */
		NONE,

		/**
		 * �ڵ�ǰ���������
		 */
		AFTER_CURRENT_CONTEXT_ALWAYS,

		/**
		 * �ڵ�ǰ����ɹ�������
		 */
		AFTER_CURRENT_CONTEXT_SUCCESS,

		/**
		 * �ڵ�ǰ�����쳣������
		 */
		AFTER_CURRENT_CONTEXT_EXCEPTION;
	}

	/**
	 * ��ȡ�滭ģʽ
	 */
	public final SessionMode getSessionMode() {
		return super.sessionMode;
	}

	/**
	 * ���ûỰģʽ
	 */
	public final void setSessionMode(SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		super.sessionMode = sessionMode;
	}

	/**
	 * �����Ƿ�����첽���õ��в�������Ϣ
	 */
	public final AsyncInfo setCareInfos(boolean value) {
		super.careInfos = value;
		return this;
	}

	/**
	 * ����Ƿ�����첽���õ��в�������Ϣ
	 */
	public final boolean isCareInfos() {
		return this.careInfos;
	}

	/**
	 * ��ʼִ�е�ʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 */
	public final long getStartTime() {
		return super.start;
	}

	/**
	 * ���ÿ�ʼִ�е�ʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 */
	public final AsyncInfo setStartTime(long value) {
		super.start = value;
		return this;
	}

	/**
	 * ��ȡִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public final long getPeriod() {
		return super.period;
	}

	/**
	 * ����ִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public final AsyncInfo setPeiod(long value) {
		super.period = value;
		return this;
	}

	public final AsyncInfo setScheduleTrigger(AwaitSchedule scheduleTrigger) {
		// TODO
		return this;
	}

	/**
	 * ���췽��
	 * 
	 * @param delay
	 *            �ӳٿ�ʼʱ��
	 * @param period
	 *            ִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public AsyncInfo(long delay, long period) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, period,
				SessionMode.SAME);
	}

	public AsyncInfo(long delay, long period, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, period,
				sessionMode);
	}

	/**
	 * ���췽����������
	 * 
	 * @param delay
	 *            �ӳٿ�ʼʱ��
	 */
	public AsyncInfo(long delay) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, 0,
				SessionMode.SAME);
	}

	public AsyncInfo(long delay, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, 0,
				sessionMode);
	}

	/**
	 * ���췽����������
	 * 
	 * @param start
	 *            ��ʼʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 * @param period
	 *            ִ�����ڣ�С�ڵ���0��ʾ������ִ��
	 */
	public AsyncInfo(Date start, long period) {
		super(start.getTime(), AwaitSchedule.NONE, period, SessionMode.SAME);
	}

	public AsyncInfo(Date start, long period, SessionMode sessionMode) {
		super(start.getTime(), AwaitSchedule.NONE, period, sessionMode);
	}

	/**
	 * ���췽��
	 * 
	 * @param start
	 *            ��ʼʱ�䣬С�ڵ�ǰʱ���ʾ����ִ��
	 */
	public AsyncInfo(Date start) {
		super(start.getTime(), AwaitSchedule.NONE, 0, SessionMode.SAME);
	}

	public AsyncInfo(Date start, SessionMode sessionMode) {
		super(start.getTime(), AwaitSchedule.NONE, 0, sessionMode);
	}

	public AsyncInfo() {
		super(0, AwaitSchedule.NONE, 0, SessionMode.SAME);
	}

	public AsyncInfo(SessionMode sessionMode) {
		super(0, AwaitSchedule.NONE, 0, sessionMode);
	}

	public AsyncInfo(AwaitSchedule awaitSchedule) {
		super(0, awaitSchedule, 0, SessionMode.SAME);
		if (awaitSchedule == null) {
			throw new NullArgumentException("awaitSchedule");
		}
	}

	public AsyncInfo(AwaitSchedule awaitSchedule, SessionMode sessionMode) {
		super(0, awaitSchedule, 0, sessionMode);
		if (awaitSchedule == null) {
			throw new NullArgumentException("awaitSchedule");
		}
	}

	public AsyncInfo(AwaitSchedule awaitSchedule, long period,
			SessionMode sessionMode) {
		super(0, awaitSchedule, period, sessionMode);
		if (awaitSchedule == null) {
			throw new NullArgumentException("awaitSchedule");
		}
	}
}
