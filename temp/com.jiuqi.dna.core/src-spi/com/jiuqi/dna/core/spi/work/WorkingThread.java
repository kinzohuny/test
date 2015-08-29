package com.jiuqi.dna.core.spi.work;


public final class WorkingThread extends Thread {

	final WorkingManager manager;

	/**
	 * 待处理的任务
	 */
	Work work;

	/**
	 * 下一个空闲线程
	 * 
	 * <p>
	 * 被WorkingManager管理的链表。
	 */
	WorkingThread nextIdle;

	/**
	 * 创建并启动线程
	 * 
	 * <p>
	 * 涉及到工作的状态的改变，当前线程与工作的提交线程不是同一个线程时需要外部锁定工作。
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
					// 这种情况是运行时造成的退出
					// 需要主动削减线程数量
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