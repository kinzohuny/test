package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataObjectTranslator;
import com.jiuqi.dna.core.type.ObjectDataType;

/**
 * ��ֵ��
 * 
 * @author gaojingxin
 * 
 */
interface ObjectDataTypeInternal extends ObjectDataType, DataTypeInternal {
	public Object assignNoCheckSrcD(DynObj dynSrc, Object dest,
			OBJAContext objaContext, DataTranslatorHelper<?, ?> dth);

	public Object assignNoCheckSrc(Object src, Object destHint,
			OBJAContext objaContext, DataTranslatorHelper<?, ?> dth);

	// ////////////////////////////////////////////////////////////
	// Serialization

	/*
	 * FIXME �������ֻ��Ȩ��֮�ƣ��д�����
	 */
	boolean supportSerialization();

	void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException,
			UnsupportedOperationException;

	Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException,
			UnsupportedOperationException;

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	public boolean nioSerializeData(final NSerializer serializer,
			final Object object);

	public DataObjectTranslator<?, ?> getDataObjectTranslator();

	public DataObjectTranslator<?, ?> registerDataObjectTranslator(
			final DataObjectTranslator<?, ?> serializer);

}
