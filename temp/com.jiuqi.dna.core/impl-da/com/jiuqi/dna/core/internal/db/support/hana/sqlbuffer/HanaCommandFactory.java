package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;

public class HanaCommandFactory implements ISqlCommandFactory,
		ISqlMergeCommandFactory {

	public static final HanaCommandFactory INSTANCE = new HanaCommandFactory();

	public <T> T getFeature(Class<T> clazz) {
		// HANA Auto-generated method stub
		return null;
	}

	public ISqlMergeBuffer merge(String table, String alias) {
		// HANA Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public HanaQueryBuffer query() {
		return new HanaQueryBuffer();
	}

	public HanaInsertBuffer insert(String table) {
		return new HanaInsertBuffer(table);
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new HanaUpdateBuffer(table, alias, assignFromSlaveTable);
	}

	public HanaDeleteBuffer delete(String table, String alias) {
		return new HanaDeleteBuffer(table, alias);
	}

	public ISqlSegmentBuffer segment() {
		// HANA Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}