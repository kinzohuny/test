package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

public class SQLServerCommandFactory implements ISqlCommandFactory {

	final SqlserverMetadata metadata;

	public SQLServerCommandFactory(SqlserverMetadata metadata) {
		this.metadata = metadata;
	}

	public ISqlQueryBuffer query() {
		return new SQLServerQueryBuffer(this.metadata, null);
	}

	public ISqlInsertBuffer insert(String table) {
		return new SQLServerInsertBuffer(this.metadata, null, table);
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new SQLServerUpdateBuffer(this.metadata, null, table, alias);
	}

	public ISqlDeleteBuffer delete(String table, String alias) {
		return new SQLServerDeleteBuffer(this.metadata, null, table, alias);
	}

	public ISqlSegmentBuffer segment() {
		return new SQLServerSegmentBuffer(this.metadata, null);
	}

	public <T> T getFeature(Class<T> clazz) {
		return null;
	}
}
