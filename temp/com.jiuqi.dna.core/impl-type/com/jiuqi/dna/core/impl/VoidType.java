package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.TypeDetector;

final class VoidType extends DataTypeBase {

	final static VoidType TYPE = new VoidType();

	/**
	 * 确保该类静态数据被JVM初始
	 */
	static void ensureStaticInited() {
	}

	private VoidType() {
		super(void.class);
	}

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_VOID);
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		throw new UnsupportedOperationException();
	}

	public AssignCapability isAssignableFrom(DataType source) {
		throw new UnsupportedOperationException();
	}

	public void digestType(Digester digester) {
		throw new UnsupportedOperationException();
	}
}
