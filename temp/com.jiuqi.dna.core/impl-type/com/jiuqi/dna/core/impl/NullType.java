package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.TypeDetector;

public final class NullType extends DataTypeBase {

	public static final NullType TYPE = new NullType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_NULL);
	}

	@Override
	public String toString() {
		return "null";
	}

	private NullType() {
		super(null);
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("¿‡–Õ");
		}
		return AssignCapability.NO;
	}

	public final void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inNull(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}
}