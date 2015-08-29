package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class OracleCommandFactory implements ISqlCommandFactory,
		ISqlMergeCommandFactory {
	static class OracleRootSegmentBuffer extends OracleSegmentBuffer {
		public OracleRootSegmentBuffer() {
			super(null);
		}

		@Override
		public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
			sql.append("begin ");
			super.writeTo(sql, args);
			sql.append(" end;");
		}
	}

	public static final OracleCommandFactory INSTANCE = new OracleCommandFactory();

	public ISqlQueryBuffer query() {
		return new OracleQueryBuffer();
	}

	public ISqlInsertBuffer insert(String table) {
		return new OracleInsertBuffer(null, table);
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		return new OracleUpdateBuffer(null, table, alias, assignFromSlaveTable);
	}

	public ISqlDeleteBuffer delete(String table, String alias) {
		return new OracleDeleteBuffer(null, table, alias);
	}

	public ISqlSegmentBuffer segment() {
		return new OracleRootSegmentBuffer();
	}

	public ISqlMergeBuffer merge(String table, String alias) {
		return new OracleMergeBuffer(null, table, alias);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFeature(Class<T> clazz) {
		if (clazz == ISqlMergeCommandFactory.class) {
			return (T) this;
		}
		return null;
	}
}
