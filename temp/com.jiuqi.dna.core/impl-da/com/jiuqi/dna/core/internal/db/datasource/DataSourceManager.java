package com.jiuqi.dna.core.internal.db.datasource;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ApplicationImpl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.RepeatWork;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.work.WorkingThread;

/**
 * ���ӳع�����
 * 
 * @author gaojingxin
 * 
 */
public final class DataSourceManager {

	public final ApplicationImpl application;

	/**
	 * ���ӵ�����
	 * 
	 * <p>
	 * �ͷŹ��ڵ��������ӣ���ʼ��С�������ȡ�
	 * 
	 * @author gaojingxin
	 */
	private final class ConnectionAdjuster extends RepeatWork {

		ConnectionAdjuster() {
			super(5000);
		}

		@Override
		protected void workDoing(WorkingThread thread) throws Throwable {
			final ArrayList<ComboDataSource> datasources = DataSourceManager.this.container;
			try {
				for (int i = 0, c = datasources.size(); i < c; i++) {
					datasources.get(i).adjust();
				}
			} catch (Throwable e) {
				DataSourceManager.this.application.catcher.catchException(e, this);
			}
		}
	}

	public DataSourceManager(ApplicationImpl application, SXElement config) {
		this.application = application;
		if (config != null) {
			for (SXElement element : config.getChildren(DataSourceManager.XML_EL_DATASOURCE)) {
				try {
					this.container.add(new ComboDataSource(this, element));
				} catch (Throwable e) {
					application.catcher.catchException(e, this);
				}
			}
		}
		application.overlappedManager.postWork(new ConnectionAdjuster());
	}

	private final NamedDefineContainerImpl<ComboDataSource> container = new NamedDefineContainerImpl<ComboDataSource>();

	public final boolean isEmpty() {
		return this.container.isEmpty();
	}

	public final ComboDataSource getDefaultSource() {
		if (this.container.isEmpty()) {
			throw new IllegalStateException("û�п��õ�����Դ����");
		}
		return this.container.get(0);
	}

	public final ComboDataSource getDataSource(String author, String name) {
		if (this.container.isEmpty()) {
			throw new IllegalStateException("û�п��õ�����Դ����");
		}
		if ((name == null) || (name.length() == 0)) {
			return this.container.get(0);
		}
		ComboDataSource ds = this.container.find(name);
		if (ds == null) {
			throw new IllegalArgumentException("�Ҳ���ָ��������Դ[" + name + "]");
		}
		return ds;
	}

	public final void doDispose() {
		try {
			for (ComboDataSource ds : this.container) {
				ds.dispose();
			}
		} catch (Throwable e) {
			this.application.catcher.catchException(e, e);
		}
	}

	public static final String XML_EL_DATASOURCES = "datasources";
	public static final String XML_EL_DATASOURCE = "datasource";
}