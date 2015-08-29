package com.jiuqi.dna.core.internal.db.datasource;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ApplicationImpl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.RepeatWork;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.work.WorkingThread;

/**
 * 连接池管理器
 * 
 * @author gaojingxin
 * 
 */
public final class DataSourceManager {

	public final ApplicationImpl application;

	/**
	 * 连接调整器
	 * 
	 * <p>
	 * 释放过期的闲置连接，创始最小连接数等。
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
			throw new IllegalStateException("没有可用的数据源配置");
		}
		return this.container.get(0);
	}

	public final ComboDataSource getDataSource(String author, String name) {
		if (this.container.isEmpty()) {
			throw new IllegalStateException("没有可用的数据源配置");
		}
		if ((name == null) || (name.length() == 0)) {
			return this.container.get(0);
		}
		ComboDataSource ds = this.container.find(name);
		if (ds == null) {
			throw new IllegalArgumentException("找不到指定的数据源[" + name + "]");
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