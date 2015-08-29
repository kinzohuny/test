package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.Type;
import com.jiuqi.dna.core.type.WritableValue;

class EnumFieldAccessor extends FieldAccessor {
	private static final EnumFieldAccessor java = new EnumFieldAccessor();
	private static final EnumFieldAccessor dyn = new EnumFieldAccessor() {
		@Override
		Object getObjectD(int offset, DynObj obj) {
			return obj.objs[offset];
		}

		@Override
		void internalSetObjD(int offset, DynObj obj, Object value) {
			obj.objs[offset] = value;
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			dest.objs[offset] = src.objs[offset];
		}
	};

	private EnumFieldAccessor() {
		super(0);
	}

	final static EnumFieldAccessor select(boolean isJavaField) {
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

	private final int getOrdinal(int offset, Object obj) {
		return ((Enum<?>) unsafe.getObject(obj, (long) offset)).ordinal();
	}

	private final int getOrdinalD(int offset, DynObj obj) {
		return ((Enum<?>) this.getObjectD(offset, obj)).ordinal();
	}

	private final void setOrdinal(StructFieldDefineImpl field, int offset,
			Object obj, int ordinal) {
		this.internalSetObj(offset, obj, ((EnumTypeImpl<?>) field.type).getEnum(ordinal));
	}

	private final void setOrdinalD(StructFieldDefineImpl field, int offset,
			DynObj obj, int ordinal) {
		this.internalSetObjD(offset, obj, ((EnumTypeImpl<?>) field.type).getEnum(ordinal));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getOrdinal(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getOrdinalD(offset, obj));
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
		return Convert.toShort(this.getOrdinal(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getOrdinalD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
	}

	@Override
	long getLong(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
	}

	@Override
	float getFloat(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
	}

	@Override
	double getDouble(int offset, Object obj) {
		return this.getOrdinal(offset, obj);
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return this.getOrdinalD(offset, obj);
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
		Object e = this.getObject(offset, obj);
		return e != null ? e.toString() : null;
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		Object e = this.getObjectD(offset, obj);
		return e != null ? e.toString() : null;
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
		this.setObject(field, offset, obj, Convert.toObject(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setObjectD(field, offset, obj, Convert.toObject(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setObject(field, offset, obj, Convert.toObject(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setObjectD(field, offset, obj, Convert.toObject(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setOrdinal(field, offset, obj, value);
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setOrdinalD(field, offset, obj, value);
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
		this.setOrdinal(field, offset, obj, value);
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setOrdinalD(field, offset, obj, value);
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setOrdinal(field, offset, obj, value);
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setOrdinalD(field, offset, obj, value);
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setOrdinal(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setOrdinalD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setOrdinal(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setOrdinalD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setOrdinal(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setOrdinalD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		Enum<?> e;
		if (value == null) {
			e = null;
		} else {
			e = ((EnumTypeImpl<?>) field.type).getEnum(value);
		}
		this.internalSetObj(offset, obj, e);
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		Enum<?> e;
		if (value == null) {
			e = null;
		} else {
			e = ((EnumTypeImpl<?>) field.type).getEnum(value);
		}
		this.internalSetObjD(offset, obj, e);
	}

	static private final Object toEnum(StructFieldDefineImpl field, Object value) {
		if (value == null) {
			return null;
		}
		return ((EnumTypeImpl<?>) field.type).convert(value);
	}

	@Override
	final void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.internalSetObj(offset, obj, toEnum(field, value));
	}

	@Override
	final void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.internalSetObjD(offset, obj, toEnum(field, value));
	}

	private final void internalSetObj(int offset, Object obj, Object value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	void internalSetObjD(int offset, DynObj obj, Object value) {
		unsafe.putObject(obj, (long) offset, value);
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		Type targetType = value.getType().getRootType();
		if (targetType == StringType.TYPE) {
			this.setString(field, offset, obj, value.getString());
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			this.setOrdinal(field, offset, obj, value.getInt());
		} else {
			this.internalSetObj(offset, obj, toEnum(field, value.getObject()));
		}
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		Type targetType = value.getType().getRootType();
		if (targetType == StringType.TYPE) {
			this.setStringD(field, offset, obj, value.getString());
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			this.setOrdinalD(field, offset, obj, value.getInt());
		} else {
			this.internalSetObjD(offset, obj, toEnum(field, value.getObject()));
		}
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		Type targetType = target.getType().getRootType();
		if (targetType == StringType.TYPE) {
			target.setString(this.getString(offset, obj));
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			target.setInt(this.getOrdinal(offset, obj));
		} else {
			target.setObject(this.getObject(offset, obj));
		}
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		Type targetType = target.getType().getRootType();
		if (targetType == StringType.TYPE) {
			target.setString(this.getStringD(offset, dynObj));
		} else if (EnumTypeImpl.ordinalSupport(targetType)) {
			target.setInt(this.getOrdinalD(offset, dynObj));
		} else {
			target.setObject(this.getObjectD(offset, dynObj));
		}
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
			InternalSerializer serializer) throws IOException {
		serializer.writeEnum((Enum<?>) this.getObject(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeEnum((Enum<?>) this.getObjectD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.internalSetObj(offset, obj, deserializer.readEnum());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.internalSetObjD(offset, dynObj, deserializer.readEnum());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeEnumField((Enum<?>) this.getObject(offset, value), (EnumTypeImpl<?>) field.type);
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeEnumField((Enum<?>) this.getObjectD(offset, value), (EnumTypeImpl<?>) field.type);
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.internalSetObj(offset, value, unserializer.readEnumField(field.type));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.internalSetObjD(offset, value, unserializer.readEnumField(field.type));
	}

}
