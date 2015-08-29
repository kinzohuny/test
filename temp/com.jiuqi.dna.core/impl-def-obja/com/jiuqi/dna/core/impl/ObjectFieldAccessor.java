package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.impl.NUnserializer.SerializeDataTranslatorHelper;
import com.jiuqi.dna.core.impl.OBJAContext.CloneDataTranslatorHelper;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class ObjectFieldAccessor extends FieldAccessor {

	private static final ObjectFieldAccessor java = new ObjectFieldAccessor();

	private static final ObjectFieldAccessor dyn = new ObjectFieldAccessor() {

		@Override
		final Object getObjectD(int offset, DynObj obj) {
			return obj.objs[offset];
		}

		@Override
		final void intrenalSetObjectD(int offset, DynObj obj, Object value) {
			obj.objs[offset] = value;
		}

	};

	private ObjectFieldAccessor() {
		super(0);
	}

	final static ObjectFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getObject(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getObjectD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getObject(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getObjectD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getObject(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getObjectD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getObject(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getObjectD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getObject(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getObjectD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getObject(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getObjectD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getObject(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getObjectD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getObject(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getObjectD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getObject(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getObjectD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getObject(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getObjectD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getObject(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getObjectD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getObject(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getObjectD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return unsafe.getObject(obj, (long) offset);
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return unsafe.getObject(obj, (long) offset);
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setObject(field, offset, obj, value);
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setObjectD(field, offset, obj, value);
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setObject(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setObjectD(field, offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setObject(field, offset, obj, value);
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setObjectD(field, offset, obj, value);
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setObject(field, offset, obj, value);
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setObjectD(field, offset, obj, value);
	}

	final void intrenalSetObject(int offset, Object obj, Object value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	void intrenalSetObjectD(int offset, DynObj obj, Object value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	final void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		unsafe.putObject(obj, (long) offset, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	final void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.intrenalSetObjectD(offset, obj, ((ObjectDataType) field.type).convert(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setObject(field, offset, obj, value.getObject());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setObjectD(field, offset, obj, value.getObject());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setObject(this.getObject(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setObject(this.getObjectD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		final long off = offset;
		final Object srcFieldObj = unsafe.getObject(src, off);
		if (srcFieldObj == null || !field.isRecursive) {
			unsafe.putObject(dest, off, srcFieldObj);
			return;
		}
		final Object destFieldObjHint = unsafe.getObject(dest, off);
		if (srcFieldObj == destFieldObjHint) {
			return;
		}
		final Object destFieldObj = objaContext.find(srcFieldObj);
		if (destFieldObj == null) {
			unsafe.putObject(dest, off, objaContext.doAssign(srcFieldObj, destFieldObjHint, field.type));
		} else if (destFieldObj.getClass() == CloneDataTranslatorHelper.class) {
			((CloneDataTranslatorHelper<?, ?>) destFieldObj).appendObjectFieldFeedbacker(dest, dth, this, offset);
		} else {
			unsafe.putObject(dest, off, destFieldObj);
		}
	}

	@Override
	final void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		final Object srcFieldObj = this.getObjectD(offset, src);
		if (srcFieldObj == null || !field.isRecursive) {
			this.intrenalSetObjectD(offset, dest, srcFieldObj);
			return;
		}
		final Object destFieldHint = this.getObjectD(offset, dest);
		if (srcFieldObj == destFieldHint) {
			return;
		}
		final Object destFieldObj = objaContext.find(srcFieldObj);
		if (destFieldObj == null) {
			this.intrenalSetObjectD(offset, dest, objaContext.doAssign(srcFieldObj, destFieldHint, field.type));
		} else if (destFieldObj.getClass() == CloneDataTranslatorHelper.class) {
			((CloneDataTranslatorHelper<?, ?>) destFieldObj).appendObjectFieldDFeedbacker(dest, dth, this, offset);
		} else {
			this.intrenalSetObjectD(offset, dest, destFieldObj);
		}
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (field.type instanceof ObjectDataTypeInternal) {
			ObjectDataTypeInternal odt = (ObjectDataTypeInternal) field.type;
			if (odt.supportSerialization()) {
				serializer.writeSpecialObject(odt, this.getObject(offset, obj));
				return;
			}
		}
		serializer.writeObject(this.getObject(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (field.type instanceof ObjectDataTypeInternal) {
			ObjectDataTypeInternal odt = (ObjectDataTypeInternal) field.type;
			if (odt.supportSerialization()) {
				serializer.writeSpecialObject(odt, this.getObjectD(offset, dynObj));
				return;
			}
		}
		serializer.writeObject(this.getObjectD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setObject(field, offset, obj, deserializer.readObject());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setObjectD(field, offset, dynObj, deserializer.readObject());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeObject(this.getObject(offset, value), field.type);
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeObject(this.getObjectD(offset, value), field.type);
	}

	@SuppressWarnings("unchecked")
	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		Object object = unserializer.readObject(field.type, hint);
		if (object != NUnserializer.UNSERIALIZABLE_OBJECT) {
			if (object != null && object.getClass() == SerializeDataTranslatorHelper.class) {
				final SerializeDataTranslatorHelper helper = (SerializeDataTranslatorHelper) object;
				helper.appendObjectFieldFeedbacker(value, dth, this, offset);
				object = null;
			}
			this.setObject(field, offset, value, object);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		Object object = unserializer.readObject(field.type, hint);
		if (object != NUnserializer.UNSERIALIZABLE_OBJECT) {
			if (object != null && object.getClass() == SerializeDataTranslatorHelper.class) {
				final SerializeDataTranslatorHelper helper = (SerializeDataTranslatorHelper) object;
				helper.appendObjectFieldDFeedbacker(value, dth, this, offset);
				object = null;
			}
			this.setObjectD(field, offset, value, object);
		}
	}

}
