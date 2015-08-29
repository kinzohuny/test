package com.jiuqi.dna.core.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;
import com.jiuqi.dna.core.type.TypeFactory;

public final class ParameterSetter extends TypeDetectorBase<Object, Object> {

	private PreparedStatementWrap pstmt;

	private int jdbcIndex;

	private StructFieldDefineImpl field;

	@Override
	public final Object inBoolean(Object obj) throws SQLException {
		final boolean b = this.field.getFieldValueAsBoolean(obj);
		this.pstmt.setBoolean(this.jdbcIndex, b);
		return null;
	}

	@Override
	public final Object inByte(Object obj) throws SQLException {
		final byte b = this.field.getFieldValueAsByte(obj);
		this.pstmt.setByte(this.jdbcIndex, b);
		return null;
	}

	@Override
	public final Object inShort(Object obj) throws SQLException {
		final short s = this.field.getFieldValueAsShort(obj);
		this.pstmt.setShort(this.jdbcIndex, s);
		return null;
	}

	@Override
	public final Object inInt(Object obj) throws SQLException {
		final int i = this.field.getFieldValueAsInt(obj);
		this.pstmt.setInt(this.jdbcIndex, i);
		return null;
	}

	@Override
	public final Object inLong(Object obj) throws SQLException {
		final long l = this.field.getFieldValueAsLong(obj);
		this.pstmt.setLong(this.jdbcIndex, l);
		return null;
	}

	@Override
	public final Object inFloat(Object obj) throws SQLException {
		final float f = this.field.getFieldValueAsFloat(obj);
		this.pstmt.setFloat(this.jdbcIndex, f);
		return null;
	}

	@Override
	public final Object inDouble(Object obj) throws SQLException {
		final double d = this.field.getFieldValueAsDouble(obj);
		this.pstmt.setDouble(this.jdbcIndex, d);
		return null;
	}

	@Override
	public final Object inString(Object obj, SequenceDataType type)
			throws SQLException {
		final String s = this.field.getFieldValueAsString(obj);
		this.pstmt.setString(this.jdbcIndex, s);
		return null;
	}

	@Override
	public final Object inBytes(Object obj, SequenceDataType type)
			throws SQLException {
		final byte[] bs = this.field.getFieldValueAsBytes(obj);
		this.pstmt.setBytes(this.jdbcIndex, bs);
		return null;
	}

	@Override
	public final Object inDate(Object obj) throws SQLException {
		final Timestamp ts = new Timestamp(this.field.getFieldValueAsDate(obj));
		this.pstmt.setTimestamp(this.jdbcIndex, ts);
		return null;
	}

	@Override
	public final Object inGUID(Object obj) throws SQLException {
		final GUID guid = this.field.getFieldValueAsGUID(obj);
		this.pstmt.setBytes(this.jdbcIndex, guid.toBytes());
		return null;
	}

	@Override
	public final Object inObject(Object obj, ObjectDataType type)
			throws Throwable {
		if (type == RefDataType.bigDecimalType) {
			Object value = this.field.getFieldValueAsObject(obj);
			BigDecimal bd = Convert.toBigDecimal(value);
			this.pstmt.setBigDecimal(this.jdbcIndex, bd);
		} else {
			throw new UnsupportedOperationException("不支持的类型[" + type + "]。");
		}
		return null;
	}

	public final void flushArgumentValues(PreparedStatementWrap ps,
			ArrayList<ParameterPlaceholder> parameters, DynObj dynObj)
			throws SQLException {
		this.pstmt = ps;
		for (int i = 0, c = parameters.size(); i < c; i++) {
			ParameterPlaceholder pr = parameters.get(i);
			if (pr instanceof ArgumentPlaceholder) {
				ArgumentPlaceholder ar = (ArgumentPlaceholder) pr;
				StructFieldDefineImpl arg = ar.arg;
				if (arg != null) {
					if (arg.isFieldValueNull(dynObj)) {
						this.pstmt.setNull(i + 1, TypeFactory.sqlTypeOf(ar.type));
					} else {
						this.field = arg;
						this.jdbcIndex = i + 1;
						ar.type.detect(this, dynObj);
					}
				}
			}
		}
	}

	public final void flushEntityValues(PreparedStatementWrap ps,
			ArrayList<ParameterPlaceholder> parameters, Object argValueObj)
			throws SQLException {
		this.pstmt = ps;
		for (int i = 0, c = parameters.size(); i < c; i++) {
			ParameterPlaceholder pr = parameters.get(i);
			if (pr instanceof ArgumentPlaceholder) {
				ArgumentPlaceholder ar = (ArgumentPlaceholder) pr;
				if (argValueObj == null) {
					throw new NullArgumentException("实体对象");
				}
				if (ar.type == DateType.TYPE && ar.arg.getFieldValueAsLong(argValueObj) == 0) {
					this.pstmt.setNull(i + 1, SQLTypesWrapper.TIMESTAMP);
				} else if (ar.arg.isFieldValueNull(argValueObj)) {
					this.pstmt.setNull(i + 1, TypeFactory.sqlTypeOf(ar.type));
				} else {
					this.field = ar.arg;
					this.jdbcIndex = i + 1;
					ar.type.detect(this, argValueObj);
				}
			}
		}
	}

	public final void flushParameters(PreparedStatementWrap ps,
			ArrayList<ParameterPlaceholder> parameters,
			DynObj parameterValueObj1, DynObj parameterValueObj2)
			throws SQLException {
		if (parameterValueObj1 == null) {
			throw new NullArgumentException("parameterValueObj1");
		}
		if (parameterValueObj2 == null) {
			throw new NullArgumentException("parameterValueObj2");
		}
		this.pstmt = ps;
		for (int i = 0, c = parameters.size(); i < c; i++) {
			ParameterPlaceholder pr = parameters.get(i);
			if (pr instanceof ArgumentPlaceholder) {
				ArgumentPlaceholder ar = (ArgumentPlaceholder) pr;
				StructFieldDefineImpl argRef = ar.arg;
				if (argRef != null) {
					DynObj argObj;
					StructDefineImpl argOwner = argRef.owner;
					if (argOwner == parameterValueObj1.define) {
						argObj = parameterValueObj1;
					} else if (argOwner == parameterValueObj2.define) {
						argObj = parameterValueObj2;
					} else {
						throw new IllegalArgumentException("无效的参数对象");
					}
					if (argRef.isFieldValueNull(argObj)) {
						this.pstmt.setNull(i, TypeFactory.sqlTypeOf(ar.type));
					} else {
						this.field = argRef;
						this.jdbcIndex = i + 1;
						ar.type.detect(this, argObj);
					}
				}
			}
		}
	}

}
