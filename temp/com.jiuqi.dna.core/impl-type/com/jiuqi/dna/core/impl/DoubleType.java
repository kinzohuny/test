/**
 *
 */
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
 * 双精度类型
 * 
 * @author gaojingxin
 * 
 */
public class DoubleType extends NumberType {

	public static final DoubleType TYPE = new DoubleType();

	@Override
	protected GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_DOUBLE);
	}

	DoubleType() {
		super(double.class);
	}

	@Override
	public final Object convert(Object from) {
		if (from == null || from.getClass() == Double.class) {
			return from;
		}
		return Convert.toDouble(from);
	}

	@Override
	public boolean canDBTypeConvertTo(DataType target) {
		return target == this || target == FloatType.TYPE;
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
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inFloat(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}

		@Override
		public AssignCapability inDouble(DataType to) throws Throwable {
			return AssignCapability.SAME;
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
				return AssignCapability.IMPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	@Override
	public NumberTypePrecedence getPrecedence() {
		return NumberTypePrecedence.DOUBLE;
	}

	@Override
	public String toString() {
		return "double";
	}

	@Override
	public final DoubleType getRootType() {
		final DoubleType type = TYPE;
		if (type != null) {
			return type;
		} else if (this.getClass() == DoubleType.class) {
			return this;
		} else {
			return TYPE;
		}
	}

	@Override
	public final Class<?> getRegClass() {
		return this.getRootType() == this ? super.getRegClass() : null;
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inDouble(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.DOUBLE);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.DOUBLE) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}
}