package com.jiuqi.dna.core.type;

import com.jiuqi.dna.core.def.model.ModelDefine;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.impl.BlobDBType;
import com.jiuqi.dna.core.impl.NTextDBType;
import com.jiuqi.dna.core.impl.TextDBType;

/**
 * 类型多态回调器
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TUserData>
 */
public abstract class TypeDetectorBase<TResult, TUserData> implements
		TypeDetector<TResult, TUserData> {
	public TResult inBoolean(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inByte(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inShort(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inInt(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inLong(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inDate(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inFloat(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inDouble(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inNumeric(TUserData userData, int precision, int scale)
			throws Throwable {
		return this.inDouble(userData);
	}

	public TResult inString(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inObject(userData, type);
	}

	public TResult inVarChar(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inString(userData, type);
	}

	public TResult inNVarChar(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inString(userData, type);
	}

	public TResult inChar(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inString(userData, type);
	}

	public TResult inNChar(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inString(userData, type);
	}

	public TResult inText(TUserData userData) throws Throwable {
		return this.inString(userData, TextDBType.TYPE);
	}

	public TResult inNText(TUserData userData) throws Throwable {
		return this.inString(userData, NTextDBType.TYPE);
	}

	public TResult inBytes(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inObject(userData, TypeFactory.BYTES);
	}

	public TResult inBinary(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inBytes(userData, type);
	}

	public TResult inVarBinary(TUserData userData, SequenceDataType type)
			throws Throwable {
		return this.inBytes(userData, type);
	}

	public TResult inBlob(TUserData userData) throws Throwable {
		return this.inBytes(userData, BlobDBType.TYPE);
	}

	public TResult inGUID(TUserData userData) throws Throwable {
		return this.inObject(userData, TypeFactory.GUID);
	}

	public TResult inEnum(TUserData userData, EnumType<?> type)
			throws Throwable {
		return this.inObject(userData, type);
	}

	public TResult inResource(TUserData userData, Class<?> facadeClass,
			Object category) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inObject(TUserData userData, ObjectDataType type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inUnknown(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inStruct(TUserData userData, StructDefine type)
			throws Throwable {
		return this.inObject(userData, type);
	}

	public TResult inModel(TUserData userData, ModelDefine type)
			throws Throwable {
		return this.inStruct(userData, type);
	}

	public TResult inQuery(TUserData userData, QueryStatementDefine type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inTable(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inRecordSet(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inNull(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public TResult inCharacter(TUserData userData) throws Throwable {
		throw new UnsupportedOperationException();
	}
}