package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 布尔类型
 * 
 * @author gaojingxin
 * 
 */
public final class BooleanType extends DataTypeBase {

	public final static BooleanType TYPE = new BooleanType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_BOOLEAN);
	}

	private BooleanType() {
		super(boolean.class);
	}

	@Override
	public final Object convert(Object from) {
		if (from == null || from.getClass() == Boolean.class) {
			return from;
		}
		return Convert.toBoolean(from);
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		return another.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inBoolean(DataType to) throws Throwable {
			return AssignCapability.SAME;
		}

		@Override
		public AssignCapability inByte(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inShort(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inInt(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inFloat(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inDouble(DataType to) throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inNumeric(DataType to, int precision, int scale)
				throws Throwable {
			return AssignCapability.EXPLICIT;
		}

		@Override
		public AssignCapability inCharacter(final DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

	};

	@Override
	public final String toString() {
		return "boolean";
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inBoolean(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.BOOLEAN);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.BOOLEAN) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

}