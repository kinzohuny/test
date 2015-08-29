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

/**
 * 短整数类型
 * 
 * @author gaojingxin
 * 
 */
public final class ShortType extends NumberType {
	public static final ShortType TYPE = new ShortType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_SHORT);
	}

	private ShortType() {
		super(short.class);
	}

	@Override
	public final Object convert(Object from) {
		if (from == null || from.getClass() == Short.class) {
			return from;
		}
		return Convert.toShort(from);
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this || target == IntType.TYPE || target == LongType.TYPE || target == DoubleType.TYPE || target == FloatType.TYPE || target instanceof NumericDBType && ((NumericDBType) target).hasIntPrecision(5);
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
			return AssignCapability.SAME;
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
		return NumberTypePrecedence.SHORT;
	}

	@Override
	public final String toString() {
		return "short";
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inShort(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.SHORT);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.SHORT) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}
}