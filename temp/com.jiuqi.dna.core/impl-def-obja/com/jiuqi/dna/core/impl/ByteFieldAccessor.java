package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class ByteFieldAccessor extends FieldAccessor {

	private static final ByteFieldAccessor java = new ByteFieldAccessor();

	private static final ByteFieldAccessor dyn = new ByteFieldAccessor() {

		@Override
		byte getByteD(int offset, DynObj obj) {
			return unsafe.getByte(obj.bin, (long) offset);
		}

		@Override
		void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
				byte value) {
			unsafe.putByte(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			long offsetL = offset;
			unsafe.putByte(dest.bin, offsetL, unsafe.getByte(src.bin, offsetL));
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xff) | (ov & 0xffffff00);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = ((ov + (int) value) & 0xff) | (ov & 0xffffff00);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private static final ByteFieldAccessor dynForIBM = new ByteFieldAccessor() {

		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		byte getByteD(int offset, DynObj obj) {
			return obj.bin[offset];
		}

		@Override
		void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
				byte value) {
			synchronized (obj) {
				obj.bin[offset] = value;
			}
		}

		@Override
		final void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			synchronized (dest) {
				dest.bin[offset] = src.bin[offset];
			}
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			synchronized (entity) {
				entity.bin[offset] += value;
			}
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			synchronized (entity) {
				entity.bin[offset] += value;
			}
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private ByteFieldAccessor() {
		super(1);
	}

	final static ByteFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getByte(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getByteD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getByte(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getByteD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return unsafe.getByte(obj, (long) offset);
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return unsafe.getByte(obj, (long) offset);
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getByte(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getByteD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getByte(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getByteD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getByte(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getByteD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getByte(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getByteD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getByte(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getByteD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getByte(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getByteD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getByte(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getByteD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getByte(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getByteD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getByte(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getByteD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getByte(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getByteD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		unsafe.putByte(obj, (long) offset, value);
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		unsafe.putByte(obj, (long) offset, value);
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setByte(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setByteD(field, offset, obj, Convert.toByte(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setByte(field, offset, obj, value.getByte());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setByteD(field, offset, obj, value.getByte());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setByte(this.getByte(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setByte(this.getByteD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putByte(dest, offsetL, unsafe.getByte(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putByte(dest, offsetL, unsafe.getByte(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeByte(this.getByte(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeByte(this.getByteD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setByte(field, offset, obj, deserializer.readByte());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setByteD(field, offset, dynObj, deserializer.readByte());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeByte(this.getByte(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeByte(this.getByteD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setByte(field, offset, value, unserializer.readByte());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setByteD(field, offset, value, unserializer.readByte());
	}

}
