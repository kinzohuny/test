package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingManager;

/**
 * ����������
 * 
 * @author gaojingxin
 * 
 */
public final class ConcurrentController {

	/**
	 * ��������
	 */
	private final int permits;

	/**
	 * ��������
	 */
	private int concurrings;

	/**
	 * �ж�
	 */
	final ConcurrentWorkQueue workqueue;

	/**
	 * �жӼ�
	 */
	ConcurrentController(ConcurrentWorkQueue workqueue, int permits) {
		if (permits <= 0) {
			throw new IllegalArgumentException("permits must > 0");
		}
		if (workqueue == null) {
			throw new NullArgumentException("workqueue");
		}
		this.workqueue = workqueue;
		this.permits = permits;
	}

	/**
	 * ���벢��
	 * 
	 * <p>
	 * ����true��ʾ�������Կ�ʼ�������ʾ������ʼ�Ŷӡ���������Ҫ��ʼ��
	 * 
	 * @param work
	 *            ����
	 */
	public final boolean enterScope(Work work) {
		// ���п�������״̬�����ж�Ϊ��
		synchronized (this.workqueue) {
			if (this.workqueue.isEmpty() && this.concurrings < this.permits) {
				this.concurrings++;
				return true;
			}
			this.workqueue.put(work);
			return false;
		}
	}

	/**
	 * �뿪����
	 * 
	 * <p>
	 * ���طǿ�ֵ��ʾ����Ҫ��������work�������õ���enter(OverlappedWork)<br>
	 * ���ؿ�ֵ��ʾû�еȴ��Ĺ���������
	 */
	public final void leaveScope(WorkingManager manager) {
		// ���п�������״̬�����ж�Ϊ��
		synchronized (this.workqueue) {
			this.concurrings--;
			for (Work work = this.workqueue.poll(); work != null; work = this.workqueue.poll()) {
				ConcurrentController ccr = work.getConcurrentController();
				// ���ж��еĹ�����ccr������Ϊ��
				if (ccr.concurrings < ccr.permits) {
					// ��ǰ�߳��빤�����ύ�̲߳�ͬ����Ҫ��������
					synchronized (work) {
						if (manager.startWork(work)) {
							ccr.concurrings++;
						}
					}
				} else {
					// �ﵽ�������ƣ�ֹͣ�ύ����
					break;
				}
			}
		}
	}
}
