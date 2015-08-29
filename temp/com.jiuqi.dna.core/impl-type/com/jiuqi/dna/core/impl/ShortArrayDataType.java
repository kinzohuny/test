/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ShortArrayDataType.java
 * Date Apr 28, 2009
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 短整数数组类型。
 * 
 * @author LRJ
 * @version 1.0
 */
public final class ShortArrayDataType extends ArrayDataTypeBase {

	public static final ShortArrayDataType TYPE = new ShortArrayDataType();

	private ShortArrayDataType() {
		super(short[].class, ShortType.TYPE);
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.SHORTS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.SHORTS) {
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
			short[] sa = (short[]) obj;
			serializer.writeInt(sa.length);
			for (int i = 0, len = sa.length; i < len; i++) {
				serializer.writeShort(sa[i]);
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
		short[] sa = new short[len];
		for (int i = 0; i < len; i++) {
			sa[i] = deserializer.readShort();
		}
		return sa;
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////
	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeShortArrayData((short[]) object);
	}

}
