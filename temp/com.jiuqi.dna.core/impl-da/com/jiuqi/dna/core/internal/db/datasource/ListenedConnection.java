package com.jiuqi.dna.core.internal.db.datasource;

public interface ListenedConnection {

	long connId();

	void addListener(PooledConnectionListener listener);

	void removeListener(PooledConnectionListener listener);
}