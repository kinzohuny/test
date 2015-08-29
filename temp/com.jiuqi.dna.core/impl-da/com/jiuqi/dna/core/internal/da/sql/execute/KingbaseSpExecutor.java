package com.jiuqi.dna.core.internal.da.sql.execute;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.BytesConstExpr;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DateConstExpr;
import com.jiuqi.dna.core.impl.IntConstExpr;
import com.jiuqi.dna.core.impl.LongConstExpr;
import com.jiuqi.dna.core.impl.RecordSetImpl;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StringConstExpr;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;

public final class KingbaseSpExecutor extends SpExecutor {

	public KingbaseSpExecutor(DBAdapterImpl adapter, SpCallSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	private static final int TYPE_CURSOR = -10;

	@Override
	public final RecordSet[] executeProcedure(Object argValueObj) {
		try {
			final StoredProcedureDefineImpl procedure = this.sql.procedure;
			final int c = this.sql.procedure.getResultSets();
			this.use(true);
			this.flushParameters(argValueObj);
			if (c == 0) {
				return EMPTY_RECORD_SETS;
			} else {
				RecordSetImpl[] recordSets = new RecordSetImpl[c];
				final int args = procedure.getArguments().size();
				for (int i = 0, j = args + 1; i < c; i++, j++) {
					this.cs.registerOutParameter(j, TYPE_CURSOR);
				}
				this.cs.execute();
				for (int i = 0, j = args + 1; i < c; i++, j++) {
					ResultSet resultSet = (ResultSet) this.cs.getObject(j);
					try {
						recordSets[i] = this.load(resultSet);
					} finally {
						resultSet.close();
					}
				}
				return recordSets;
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private static final HashMap<String, ConstExpr> ora_map = new HashMap<String, ConstExpr>();
	static {
		try {
			ora_map.put("kingbase.sql.CLOB", StringConstExpr.EMPTY);
			ora_map.put("kingbase.sql.BLOB", BytesConstExpr.EMPTY);
			ora_map.put("byte[]", BytesConstExpr.EMPTY);
			ora_map.put("kingbase.sql.TIMESTAMP", DateConstExpr.ZERO);
			ora_map.put("kingbase.sql.TIMESTAMPTZ", DateConstExpr.ZERO);
			ora_map.put("kingbase.sql.TIMESTAMPLTZ", DateConstExpr.ZERO);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	static final String NUMBER_CLASS = BigDecimal.class.getName();

	@Override
	final ConstExpr constOf(ResultSetMetaData rsmd, int j) throws SQLException {
		final String className = rsmd.getColumnClassName(j);
		if (className == null) {
			return null;
		} else if (className.equals(NUMBER_CLASS)) {
			final int precision = rsmd.getPrecision(j);
			final int scale = rsmd.getScale(j);
			if (scale == 0) {
				if (precision <= 10) {
					return IntConstExpr.ZERO_INT;
				} else if (scale <= 19) {
					return LongConstExpr.ZERO_LONG;
				}
			}
		}
		final ConstExpr c = defaults.get(className);
		if (c != null) {
			return c;
		} else {
			return ora_map.get(className);
		}
	}
}
