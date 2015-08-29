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
 * ��������
 * 
 * @author gaojingxin
 * 
 */
public final class DateType extends DataTypeBase {

	public static final DateType TYPE = new DateType();

	@Override
	public Class<?> getRegClass() {
		return null;
	}

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_DATE);
	}

	private DateType() {
		super(long.class);
		this.setArrayOf(LongArrayDataType.TYPE);
	}

	@Override
	public final Object convert(Object from) {
		if (from == null) {
			return from;
		}
		return Convert.toDate(from);
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("����");
		}
		return another.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inLong(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inDate(DataType to) throws Throwable {
			return AssignCapability.SAME;
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

	public final boolean accept(DataType type) {
		return type == TYPE;
	}

	@Override
	public final String toString() {
		return "date";
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inDate(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.DATE);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.DATE) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}
}