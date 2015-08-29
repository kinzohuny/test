package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class DateFieldAccessor extends FieldAccessor {
	private static final DateFieldAccessor java = new DateFieldAccessor();
	private static final DateFieldAccessor dyn = new DateFieldAccessor() {
		@Override
		long getDateD(int offset, DynObj obj) {
			return unsafe.getLong(obj.bin, (long) offset);
		}

		@Override
		void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
				long value) {
			unsafe.putLong(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			long offsetL = offset;
			unsafe.putLong(dest.bin, offsetL, unsafe.getLong(src.bin, offsetL));
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			unsafe.putLong(entity.bin, (long) offset, value);
		}
	};

	private static final DateFieldAccessor dynForIBM = new DateFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		long getDateD(int offset, DynObj obj) {
			synchronized (obj) {
				long v = obj.bin[offset++];
				for (int i = 1; i < 8; i++) {
					v = v << 8 | (obj.bin[offset++] & 0xFF);
				}
				return v;
			}
		}

		@Override
		void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
				long value) {
			synchronized (obj) {
				offset += 7;
				obj.bin[offset--] = (byte) value;
				for (int i = 1; i < 8; i++) {
					value >>>= 8;
					obj.bin[offset--] = (byte) value;
				}
			}
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			byte[] arr = new byte[8];
			synchronized (src) {
				for (int i = 0; i < 8; i++) {
					arr[i] = src.bin[offset + i];
				}
			}
			synchronized (dest) {
				for (int i = 0; i < 8; i++) {
					dest.bin[offset + i] = arr[i];
				}
			}
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			this.setDateD(field, offset, entity, value);
		}
	};

	private DateFieldAccessor() {
		super(8);
	}

	final static DateFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getDate(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getDateD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getDate(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getDateD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getDate(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getDateD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getDate(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getDateD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getDate(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getDateD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getDate(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getDateD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getDate(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getDateD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getDate(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getDateD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getDate(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getDateD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.dateToString(this.getDate(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.dateToString(this.getDateD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getDate(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getDateD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.dateToObject(this.getDate(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.dateToObject(this.getDateD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setDate(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setDateD(field, offset, obj, Convert.toDate(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setDate(field, offset, obj, value.getDate());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setDateD(field, offset, obj, value.getDate());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setDate(this.getDate(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setDate(this.getDateD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putLong(dest, offsetL, unsafe.getLong(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putLong(dest, offsetL, unsafe.getLong(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeLong(this.getDate(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeLong(this.getDateD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDate(field, offset, obj, deserializer.readLong());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDateD(field, offset, dynObj, deserializer.readLong());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeDateField(this.getDate(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeDateField(this.getDateD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setDate(field, offset, value, unserializer.readDateField());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setDateD(field, offset, value, unserializer.readChar());
	}

}
