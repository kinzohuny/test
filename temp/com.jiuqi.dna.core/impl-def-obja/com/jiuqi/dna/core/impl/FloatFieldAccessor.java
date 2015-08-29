package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class FloatFieldAccessor extends FieldAccessor {
	private static final FloatFieldAccessor java = new FloatFieldAccessor();
	private static final FloatFieldAccessor dyn = new FloatFieldAccessor() {
		@Override
		float getFloatD(int offset, DynObj obj) {
			return unsafe.getFloat(obj.bin, (long) offset);
		}

		@Override
		void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
				float value) {
			unsafe.putFloat(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			long offsetL = offset;
			unsafe.putFloat(dest.bin, offsetL, unsafe.getFloat(src.bin, offsetL));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			final long of = offset;
			final Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = Float.floatToRawIntBits((float) (Float.intBitsToFloat(ov) + value));
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			final long of = offset;
			final Object obj = entity.bin;
			int ov;
			int nv;
			do {
				ov = unsafe.getIntVolatile(obj, of);
				nv = Float.floatToRawIntBits(Float.intBitsToFloat(ov) + value);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private static final FloatFieldAccessor dynForIBM = new FloatFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		float getFloatD(int offset, DynObj obj) {
			synchronized (obj) {
				return this.internalGetFloatD(offset, obj);
			}
		}

		private final float internalGetFloatD(int offset, DynObj obj) {
			int v = obj.bin[offset++];
			for (int i = 1; i < 4; i++) {
				v = v << 8 | (obj.bin[offset++] & 0xFF);
			}
			return Float.intBitsToFloat(v);
		}

		@Override
		void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
				float value) {
			synchronized (obj) {
				this.internalSetFloatD(field, offset, obj, value);
			}
		}

		private final void internalSetFloatD(StructFieldDefineImpl field,
				int offset, DynObj obj, float value) {
			int v = Float.floatToRawIntBits(value);
			offset += 3;
			obj.bin[offset--] = (byte) v;
			for (int i = 1; i < 4; i++) {
				v >>>= 8;
				obj.bin[offset--] = (byte) v;
			}
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			byte[] arr = new byte[4];
			synchronized (src) {
				for (int i = 0; i < 4; i++) {
					arr[i] = src.bin[offset + i];
				}
			}
			synchronized (dest) {
				for (int i = 0; i < 4; i++) {
					dest.bin[offset + i] = arr[i];
				}
			}
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			synchronized (entity) {
				this.internalSetFloatD(field, offset, entity, (float) (this.internalGetFloatD(offset, entity) + value));
			}
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			synchronized (entity) {
				this.internalSetFloatD(field, offset, entity, this.internalGetFloatD(offset, entity) + value);
			}
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private FloatFieldAccessor() {
		super(4);
	}

	final static FloatFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getFloat(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getFloatD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getFloat(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getFloatD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getFloat(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getFloatD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getFloat(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getFloatD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getFloat(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getFloatD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getFloat(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getFloatD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getFloat(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getFloatD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return unsafe.getFloat(obj, (long) offset);
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return unsafe.getFloat(obj, (long) offset);
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getFloat(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getFloatD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getFloat(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getFloatD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getFloat(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getFloatD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getFloat(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getFloatD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getFloat(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getFloatD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		unsafe.putFloat(obj, (long) offset, value);
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		unsafe.putFloat(obj, (long) offset, value);
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setFloat(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setFloatD(field, offset, obj, Convert.toFloat(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setFloat(field, offset, obj, value.getFloat());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setFloatD(field, offset, obj, value.getFloat());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setFloat(this.getFloat(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setFloat(this.getFloatD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putFloat(dest, offsetL, unsafe.getFloat(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putFloat(dest, offsetL, unsafe.getFloat(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeFloat(this.getFloat(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeFloat(this.getFloatD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setFloat(field, offset, obj, deserializer.readFloat());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setFloatD(field, offset, dynObj, deserializer.readFloat());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeFloat(this.getFloat(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeFloat(this.getFloatD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setFloat(field, offset, value, unserializer.readFloat());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setFloatD(field, offset, value, unserializer.readFloat());
	}

}
