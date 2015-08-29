package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteMultiCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlReplaceCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateMultiCommandFactory;

public final class MysqlCommandFactory implements ISqlCommandFactory,
		ISqlReplaceCommandFactory, ISqlUpdateMultiCommandFactory,
		ISqlDeleteMultiCommandFactory {

	public static final MysqlCommandFactory INSTANCE = new MysqlCommandFactory();

	private MysqlCommandFactory() {
	}

	public final MysqlQueryBuffer query() {
		return new MysqlQueryBuffer();
	}

	public final MysqlInsertBuffer insert(String table) {
		return new MysqlInsertBuffer(null, table);
	}

	public final MysqlDeleteBuffer delete(String table, String alias) {
		return new MysqlDeleteBuffer(null, table, alias);
	}

	public final MysqlDeleteMultiBuffer deleteMulti(String table, String alias) {
		return new MysqlDeleteMultiBuffer(null, table, alias);
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new MysqlUpdateBuffer(null, table, alias, assignFromSlaveTable);
	}

	public final MysqlUpdateMultiBuffer updateMultiple(String table,
			String alias) {
		return new MysqlUpdateMultiBuffer(null, table, alias);
	}

	public final MysqlReplaceBuffer replace(String table) {
		return new MysqlReplaceBuffer(null, table);
	}

	public final ISqlSegmentBuffer segment() {
		return new MysqlSegmentBuffer(null);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getFeature(Class<T> clazz) {
		if (clazz == ISqlReplaceCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlUpdateMultiCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlDeleteMultiCommandFactory.class) {
			return (T) this;
		}
		return null;
	}

}
