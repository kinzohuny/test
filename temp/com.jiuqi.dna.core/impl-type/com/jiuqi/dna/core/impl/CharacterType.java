package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;

public final class CharacterType extends DataTypeBase {

	public static final CharacterType TYPE = new CharacterType();

	private CharacterType() {
		super(char.class);
	}

	@Override
	public final Object convert(Object from) {
		if (from == null || from.getClass() == Character.class) {
			return from;
		}
		return Convert.toChar(from);
	}

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(PRIMITIVE_TYPE_CHAR);
	}

	@Override
	public final boolean canDBTypeConvertTo(final DataType target) {
		return false;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			final TypeDetector<TResult, TUserData> caller,
			final TUserData userData) {
		try {
			return caller.inCharacter(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final AssignCapability isAssignableFrom(final DataType source) {
		if (source == null) {
			throw new NullArgumentException("¿‡–Õ");
		}
		return source.detect(assignbility, this);
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inBoolean(DataType to) throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public final AssignCapability inCharacter(final DataType to)
				throws Throwable {
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

	public void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final String toString() {
		return "character";
	}

	@Override
	public final boolean isDBType() {
		return false;
	}
}
