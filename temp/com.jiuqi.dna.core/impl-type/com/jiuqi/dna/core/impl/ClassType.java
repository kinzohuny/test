/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ClassType.java
 * Date Apr 27, 2009
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
public final class ClassType extends ObjectDataTypeBase {

	public static final ClassType TYPE = new ClassType();

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_CLASS);
	}

	private ClassType() {
		super(Class.class);
	}

	@Override
	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("¿‡–Õ");
		}
		if (another == this) {
			return AssignCapability.SAME;
		}
		return AssignCapability.NO;
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.CLASS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.CLASS) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeClassData((Class<?>) object);
	}

}
