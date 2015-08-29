package com.jiuqi.dna.core.spi.work;


public final class WorkingThread extends Thread {

	final WorkingManager manager;

	/**
	 * �����������
	 */
	Work work;

	/**
	 * ��һ�������߳�
	 * 
	 * <p>
	 * ��WorkingManager���������
	 */
	WorkingThread nextIdle;

	/**
	 * �����������߳�
	 * 
	 * <p>
	 * �漰��������״̬�ĸı䣬��ǰ�߳��빤�����ύ�̲߳���ͬһ���߳�ʱ��Ҫ�ⲿ����������
	 */
	WorkingThread(WorkingManager manager, String name) {
		super(manager.threadGroup, name);
		this.manager = manager;
		this.setContextClassLoader(manager.getContextClassLoader());
		this.start();
	}

//	public static final ClassLoader cxfBundleClassLoader = org.apache.cxf.BusFactory.class.getClassLoader();

	@Override
	public final void run() {
		Work work = null;
		try {
			try {
				for (;;) {
					work = this.manager.getWorkToDo(this);
					if (work != null) {
						work.doWork(this);
					} else {
						break;
					}
				}
			} finally {
				if (work != null) {
					// �������������ʱ��ɵ��˳�
					// ��Ҫ���������߳�����
					this.manager.threadExit(this);
				}
			}
		} catch (Throwable e) {
			try {
				this.manager.application.catcher.catchException(e, work == null ? this : work);
			} catch (Throwable e2) {
			}
		}
	}
}