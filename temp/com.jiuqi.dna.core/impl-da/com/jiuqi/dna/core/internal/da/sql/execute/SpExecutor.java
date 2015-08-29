package com.jiuqi.dna.core.internal.da.sql.execute;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.model.ModelDefine;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.ArgumentOutput;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.BooleanConstExpr;
import com.jiuqi.dna.core.impl.ByteConstExpr;
import com.jiuqi.dna.core.impl.BytesConstExpr;
import com.jiuqi.dna.core.impl.BytesType;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DateConstExpr;
import com.jiuqi.dna.core.impl.DoubleConstExpr;
import com.jiuqi.dna.core.impl.FloatConstExpr;
import com.jiuqi.dna.core.impl.IntConstExpr;
import com.jiuqi.dna.core.impl.LongConstExpr;
import com.jiuqi.dna.core.impl.ProcedureExpectedResultSetsDissatifiedException;
import com.jiuqi.dna.core.impl.QueryStatementImpl;
import com.jiuqi.dna.core.impl.RecordSetImpl;
import com.jiuqi.dna.core.impl.RefDataType;
import com.jiuqi.dna.core.impl.ShortConstExpr;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.StringConstExpr;
import com.jiuqi.dna.core.impl.StringType;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.db.datasource.CallableStatementWrap;
import com.jiuqi.dna.core.type.EnumType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;

public abstract class SpExecutor extends
		SimpleSqlExecutor<SpCallSql, SpExecutor> implements
		TypeDetector<Object, Object> {

	static final RecordSet[] EMPTY_RECORD_SETS = new RecordSet[] {};

	SpExecutor(DBAdapterImpl adapter, SpCallSql sql, ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	@Override
	public final void use(boolean forUpdate) throws SQLException {
		if (this.pstmt == null) {
			this.pstmt = this.cs = this.adapter.prepareCall(this.sql.text());
			this.activeChanged(true);
		}
		this.adapter.updateTrans(forUpdate);
	}

	@Override
	public final void unuse() {
		super.unuse();
		this.cs = null;
	}

	CallableStatementWrap cs;

	public abstract RecordSet[] executeProcedure(Object argValueObj);

	final RecordSetImpl load(ResultSet resultSet) throws SQLException {
		final ResultSetMetaData rsmd = resultSet.getMetaData();
		final int count = rsmd.getColumnCount();
		QueryStatementImpl query = new QueryStatementImpl("shell");
		query.newReference(TableDefineImpl.DUMMY);
		for (int i = 0, j = 1; i < count; i++, j++) {
			final String alias = rsmd.getColumnName(j);
			ConstExpr ce = this.constOf(rsmd, j);
			if (ce == null) {
				final String columnTypeName = rsmd.getColumnTypeName(j);
				final String columnClassName = rsmd.getColumnClassName(j);
				throw new UnsupportedOperationException("不支持结果集列的数据类型[" + columnTypeName + "],其className为[" + columnClassName + "].");
			}
			query.newColumn(ce, alias);
		}
		query.ensurePrepared();
		RecordSetImpl recordSet = new RecordSetImpl(query);
		recordSet.loadRecordSet(resultSet);
		return recordSet;
	}

	static final HashMap<String, ConstExpr> defaults = new HashMap<String, ConstExpr>();
	static {
		try {
			defaults.put(Boolean.class.getName(), BooleanConstExpr.FALSE);
			defaults.put(Byte.class.getName(), ByteConstExpr.ZERO_BYTE);
			defaults.put(Short.class.getName(), ShortConstExpr.ZERO_SHORT);
			defaults.put(Integer.class.getName(), IntConstExpr.ZERO_INT);
			defaults.put(Long.class.getName(), LongConstExpr.ZERO_LONG);
			defaults.put(BigDecimal.class.getName(), DoubleConstExpr.ZERO_DOUBLE);
			defaults.put(Float.class.getName(), FloatConstExpr.ZERO_FLOAT);
			defaults.put(Double.class.getName(), DoubleConstExpr.ZERO_DOUBLE);
			defaults.put(String.class.getName(), StringConstExpr.EMPTY);
			defaults.put(Clob.class.getName(), StringConstExpr.EMPTY);
			defaults.put(Blob.class.getName(), BytesConstExpr.EMPTY);
			defaults.put(java.util.Date.class.getName(), DateConstExpr.ZERO);
			defaults.put(java.sql.Date.class.getName(), DateConstExpr.ZERO);
			defaults.put(Timestamp.class.getName(), DateConstExpr.ZERO);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	abstract ConstExpr constOf(ResultSetMetaData rsmd, int j)
			throws SQLException;

	final RecordSet[] loadUsingJdbcMoreResultSet(Object argValueObj) {
		try {
			final StoredProcedureDefineImpl procedure = this.sql.procedure;
			final int c = this.sql.procedure.getResultSets();
			this.use(true);
			this.flushParameters(argValueObj);
			final boolean r = this.cs.execute();
			if (c == 0) {
				if (r) {
					throw new ProcedureExpectedResultSetsDissatifiedException(procedure, 1);
				}
				return EMPTY_RECORD_SETS;
			} else if (r) {
				RecordSet[] recordSets = new RecordSet[c];
				ResultSet resultSet = null;
				int loaded = 0;
				try {
					do {
						resultSet = this.pstmt.getResultSet();
						try {
							recordSets[loaded] = this.load(resultSet);
							loaded++;
						} finally {
							resultSet.close();
						}
						// TODO 可能中间带有返回更新计数的场景?
					} while (this.pstmt.getMoreResults() && loaded < c);
					if (loaded == c) {
						return recordSets;
					} else {
						throw new ProcedureExpectedResultSetsDissatifiedException(procedure, loaded);
					}
				} catch (SQLException e) {
					throw Utils.tryThrowException(new RuntimeException(e));
				}
			} else {
				throw new ProcedureExpectedResultSetsDissatifiedException(procedure, 0);
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	StructFieldDefineImpl outputArgument;
	int outputIndex;

	protected final void loadOutputArgument(Object argValueObj) {
		for (int i = this.sql.procedure.getArguments().size() - 1; i >= 0; i--) {
			StructFieldDefineImpl sf = this.sql.procedure.getArguments().get(i);
			if (sf.output == ArgumentOutput.OUT || sf.output == ArgumentOutput.IN_OUT) {
				this.outputArgument = sf;
				this.outputIndex = i + 1;
				sf.getType().detect(this, argValueObj);
			} else {
				break;
			}
		}
	}

	public Object inBoolean(Object argValueObj) throws Throwable {
		final boolean value = this.cs.getBoolean(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsBoolean(argValueObj, value);
		}
		return null;
	}

	public Object inByte(Object argValueObj) throws Throwable {
		final byte value = this.cs.getByte(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsByte(argValueObj, value);
		}
		return null;
	}

	public Object inShort(Object argValueObj) throws Throwable {
		final short value = this.cs.getShort(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsShort(argValueObj, value);
		}
		return null;
	}

	public Object inInt(Object argValueObj) throws Throwable {
		final int value = this.cs.getInt(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsInt(argValueObj, value);
		}
		return null;
	}

	public Object inLong(Object argValueObj) throws Throwable {
		final long value = this.cs.getLong(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsLong(argValueObj, value);
		}
		return null;
	}

	public Object inDate(Object argValueObj) throws Throwable {
		final Timestamp value = this.cs.getTimestamp(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsDate(argValueObj, value.getTime());
		}
		return null;
	}

	public Object inFloat(Object argValueObj) throws Throwable {
		final float value = this.cs.getFloat(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsFloat(argValueObj, value);
		}
		return null;
	}

	public Object inDouble(Object argValueObj) throws Throwable {
		final double value = this.cs.getDouble(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsDouble(argValueObj, value);
		}
		return null;
	}

	public Object inNumeric(Object argValueObj, int precision, int scale)
			throws Throwable {
		return this.inDouble(argValueObj);
	}

	public Object inString(Object argValueObj, SequenceDataType type)
			throws Throwable {
		final String value = this.cs.getString(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsString(argValueObj, value);
		}
		return null;
	}

	public Object inObject(Object argValueObj, ObjectDataType type)
			throws Throwable {
		if (type == RefDataType.bigDecimalType) {
			final BigDecimal value = this.cs.getBigDecimal(this.outputIndex);
			if (this.cs.wasNull()) {
				this.outputArgument.setFieldValueNull(argValueObj);
			} else {
				this.outputArgument.setFieldValueAsObject(argValueObj, value);
			}
		} else {
			final Object value = this.cs.getObject(this.outputIndex);
			if (this.cs.wasNull()) {
				this.outputArgument.setFieldValueNull(argValueObj);
			} else {
				this.outputArgument.setFieldValueAsObject(argValueObj, value);
			}
		}
		return null;
	}

	public Object inVarChar(Object argValueObj, SequenceDataType type)
			throws Throwable {
		return this.inString(argValueObj, StringType.TYPE);
	}

	public Object inNVarChar(Object argValueObj, SequenceDataType type)
			throws Throwable {
		return this.inString(argValueObj, StringType.TYPE);
	}

	public Object inChar(Object argValueObj, SequenceDataType type)
			throws Throwable {
		return this.inString(argValueObj, StringType.TYPE);
	}

	public Object inNChar(Object argValueObj, SequenceDataType type)
			throws Throwable {
		return this.inString(argValueObj, StringType.TYPE);
	}

	public Object inText(Object argValueObj) throws Throwable {
		return this.inString(argValueObj, StringType.TYPE);
	}

	public Object inNText(Object argValueObj) throws Throwable {
		return this.inString(argValueObj, StringType.TYPE);
	}

	public Object inBytes(Object argValueObj, SequenceDataType type)
			throws Throwable {
		final byte[] value = this.cs.getBytes(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			this.outputArgument.setFieldValueAsBytes(argValueObj, value);
		}
		return null;
	}

	public Object inBinary(Object argValueObj, SequenceDataType type)
			throws Throwable {
		return this.inBytes(argValueObj, BytesType.TYPE);
	}

	public Object inVarBinary(Object argValueObj, SequenceDataType type)
			throws Throwable {
		return this.inBytes(argValueObj, BytesType.TYPE);
	}

	public Object inBlob(Object argValueObj) throws Throwable {
		return this.inBytes(argValueObj, BytesType.TYPE);
	}

	public Object inGUID(Object argValueObj) throws Throwable {
		final byte[] value = this.cs.getBytes(this.outputIndex);
		if (this.cs.wasNull()) {
			this.outputArgument.setFieldValueNull(argValueObj);
		} else {
			final GUID guid = GUID.valueOf(value);
			this.outputArgument.setFieldValueAsGUID(argValueObj, guid);
		}
		return null;
	}

	public Object inEnum(Object argValueObj, EnumType<?> type) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inResource(Object argValueObj, Class<?> facadeClass,
			Object category) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inUnknown(Object argValueObj) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inStruct(Object argValueObj, StructDefine type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inModel(Object argValueObj, ModelDefine type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inQuery(Object argValueObj, QueryStatementDefine type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inTable(Object argValueObj) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inRecordSet(Object argValueObj) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inNull(Object argValueObj) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inCharacter(Object argValueObj) throws Throwable {
		throw new UnsupportedOperationException();
	}
}