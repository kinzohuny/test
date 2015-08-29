package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class BytesFieldAccessor extends FieldAccessor {
	private static final BytesFieldAccessor java = new BytesFieldAccessor();
	private static final BytesFieldAccessor dyn = new BytesFieldAccessor() {
		@Override
		byte[] getBytesD(int offset, DynObj obj) {
			return (byte[]) obj.objs[offset];
		}

		@Override
		void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
				byte[] value) {
			obj.objs[offset] = value;
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			dest.objs[offset] = src.objs[offset];
		}

	};

	private BytesFieldAccessor() {
		super(0);
	}

	final static BytesFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getBytes(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getBytesD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getBytes(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getBytesD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getBytes(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getBytesD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return (byte[]) unsafe.getObject(obj, (long) offset);
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return (byte[]) unsafe.getObject(obj, (long) offset);
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getBytes(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getBytesD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getBytes(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getBytesD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getBytes(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getBytesD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getBytes(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getBytesD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getBytes(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getBytesD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getBytes(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getBytesD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getBytes(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getBytesD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getBytes(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getBytesD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return this.getBytes(offset, obj);
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return this.getBytesD(offset, obj);
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setBytes(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setBytesD(field, offset, obj, Convert.toBytes(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setBytes(field, offset, obj, value.getBytes());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setBytesD(field, offset, obj, value.getBytes());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setBytes(this.getBytes(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setBytes(this.getBytesD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putObject(dest, offsetL, unsafe.getObject(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putObject(dest, offsetL, unsafe.getObject(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (field.type == BytesType.TYPE) {
			serializer.writeSpecialObject(BytesType.TYPE, this.getObject(offset, obj));
		} else {
			throw new UnsupportedOperationException("Illegal data type: " + field.type);
		}
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException,
			StructDefineNotFoundException {
		if (field.type == BytesType.TYPE) {
			serializer.writeSpecialObject(BytesType.TYPE, this.getObjectD(offset, dynObj));
		} else {
			throw new UnsupportedOperationException("Illegal data type: " + field.type);
		}
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		if (field.type == BytesType.TYPE) {
			this.setObject(field, offset, obj, deserializer.readObject());
		} else {
			throw new UnsupportedOperationException("Illegal data type: " + field.type);
		}
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		if (field.type == BytesType.TYPE) {
			this.setObjectD(field, offset, dynObj, deserializer.readObject());
		} else {
			throw new UnsupportedOperationException("Illegal data type: " + field.type);
		}
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeByteArrayField(this.getBytes(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeByteArrayField(this.getBytesD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		final Object object = unserializer.readByteArrayField();
		this.setBytes(field, offset, value, object == null ? null : (byte[]) object);
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		final Object object = unserializer.readByteArrayField();
		this.setBytesD(field, offset, value, object == null ? null : (byte[]) object);
	}

}
