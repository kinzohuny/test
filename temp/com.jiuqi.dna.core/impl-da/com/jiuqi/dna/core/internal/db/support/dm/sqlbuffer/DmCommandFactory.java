package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;

public class DmCommandFactory implements ISqlCommandFactory {

	public static final DmCommandFactory INSTANCE = new DmCommandFactory();

	public <T> T trait(Class<T> trait) {
		return null;
	}

	public final DmQueryBuffer query() {
		return new DmQueryBuffer();
	}

	public final DmInsertBuffer insert(String table) {
		return new DmInsertBuffer(table);
	}

	public final DmUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new DmUpdateBuffer(table, alias, assignFromSlaveTable);
	}

	public final DmDeleteBuffer delete(String table, String alias) {
		return new DmDeleteBuffer(table, alias);
	}

	public ISqlSegmentBuffer segment() {
		throw new UnsupportedOperationException();
	}

	public <T> T getFeature(Class<T> clazz) {
		return null;
	}
}