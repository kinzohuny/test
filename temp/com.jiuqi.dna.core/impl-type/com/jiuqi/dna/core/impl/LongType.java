package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.Undigester;

public final class LongType extends NumberType {

	public static final LongType TYPE = new LongType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_LONG);
	}

	private LongType() {
		super(long.class);
	}

	@Override
	public final Object convert(Object from) {
		if (from == null || from.getClass() == Long.class) {
			return from;
		}
		return Convert.toLong(from);
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this || target == DoubleType.TYPE || target == FloatType.TYPE || target instanceof NumericDBType && ((NumericDBType) target).hasIntPrecision(19);
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
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inByte(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inShort(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inInt(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.SAME;
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
		public AssignCapability inCharacter(final DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inDate(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inObject(DataType to, ObjectDataType type)
				throws Throwable {
			if (type == RefDataType.bigDecimalType || type == RefDataType.bigIntegerType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	@Override
	public final NumberTypePrecedence getPrecedence() {
		return NumberTypePrecedence.LONG;
	}

	public final boolean accept(DataType type) {
		return type == BooleanType.TYPE || type == ByteType.TYPE || type == ShortType.TYPE || type == IntType.TYPE || type == LongType.TYPE;
	}

	@Override
	public final String toString() {
		return "long";
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inLong(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.LONG);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.LONG) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}
}