package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.Undigester;

/**
 * Î´ÖªÀàÐÍ
 * 
 * @author gaojingxin
 * 
 */
public final class UnknownType extends DataTypeBase {

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_UNKNOWN);
	}

	@Override
	public String toString() {
		return "unknown";
	}

	private UnknownType() {
		super(null);
	}

	public final AssignCapability isAssignableFrom(DataType another) {
		return AssignCapability.NO;
	}

	public static final UnknownType TYPE = new UnknownType();

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inUnknown(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.UNKNOWN) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				throw new UnsupportedOperationException();
			}
		});
	}

}