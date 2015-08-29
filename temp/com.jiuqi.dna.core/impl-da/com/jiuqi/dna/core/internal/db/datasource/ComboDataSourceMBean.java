package com.jiuqi.dna.core.internal.db.datasource;

public interface ComboDataSourceMBean {

	public int getMaxConnectionCount();

	public int getMinConnectionCount();

	public int getActiveCount();

	public int getUsingCount();

	public int getWaitingCount();
}