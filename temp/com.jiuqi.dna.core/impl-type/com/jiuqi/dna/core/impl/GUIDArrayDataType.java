/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File LongArrayDataType.java
 * Date Apr 28, 2009
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.Undigester;

/**
 * GUID数组类型。
 * 
 * @author LRJ
 * @version 1.0
 */
public final class GUIDArrayDataType extends ObjectArrayDataType {

	public static final GUIDArrayDataType TYPE = new GUIDArrayDataType();

	private GUIDArrayDataType() {
		super(GUID[].class, GUIDType.TYPE);
	}

	@Override
	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.GUIDS);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.GUIDS) {
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
			GUID[] la = (GUID[]) obj;
			serializer.writeInt(la.length);
			for (GUID guid : la) {
				serializer.writeLong(guid.getMostSigBits());
				serializer.writeLong(guid.getLeastSigBits());
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
		GUID[] la = new GUID[len];
		for (int i = 0; i < len; i++) {
			la[i] = GUID.valueOf(deserializer.readLong(), deserializer.readLong());
		}
		return la;
	}
}
