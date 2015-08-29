/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File IntArrayDataType.java
 * Date Apr 28, 2009
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 整数数组类型。
 * 
 * @author LRJ
 * @version 1.0
 */
public final class IntArrayDataType extends ArrayDataTypeBase {

	public static final IntArrayDataType TYPE = new IntArrayDataType();

	private IntArrayDataType() {
		super(int[].class, IntType.TYPE);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.INTS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.INTS) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	// //////////////////////////////////
	// Serialization

	@Override
	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		if (obj == null) {
			serializer.writeInt(-1);
		} else {
			int[] ia = (int[]) obj;
			serializer.writeInt(ia.length);
			for (int i = 0, len = ia.length; i < len; i++) {
				serializer.writeInt(ia[i]);
			}
		}
	}

	@Override
	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		int len = deserializer.readInt();
		if (len == -1) {
			return null;
		}
		int[] ia = new int[len];
		for (int i = 0; i < len; i++) {
			ia[i] = deserializer.readInt();
		}
		return ia;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////
	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeIntArrayData((int[]) object);
	}

}