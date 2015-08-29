package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class LongFieldAccessor extends FieldAccessor {
	private static final LongFieldAccessor java = new LongFieldAccessor();
	private static final LongFieldAccessor dyn = new LongFieldAccessor() {
		@Override
		long getLongD(int offset, DynObj obj) {
			return unsafe.getLong(obj.bin, (long) offset);
		}

		@Override
		void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
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
			long of = offset;
			Object obj = entity.bin;
			long ov;
			do {
				ov = unsafe.getLongVolatile(obj, of);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, ov + value));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			long of = offset;
			Object obj = entity.bin;
			long ov;
			do {
				ov = unsafe.getLongVolatile(obj, of);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, ov + (long) value));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private static final LongFieldAccessor dynForIBM = new LongFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		long getLongD(int offset, DynObj obj) {
			synchronized (obj) {
				return this.internalGetLongD(offset, obj);
			}
		}

		private final long internalGetLongD(int offset, DynObj obj) {
			long v = obj.bin[offset++];
			for (int i = 1; i < 8; i++) {
				v = v << 8 | (obj.bin[offset++] & 0xFF);
			}
			return v;
		}

		@Override
		void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
				long value) {
			synchronized (obj) {
				this.internalSetLongD(field, offset, obj, value);
			}
		}

		private final void internalSetLongD(StructFieldDefineImpl field,
				int offset, DynObj obj, long value) {
			offset += 7;
			obj.bin[offset--] = (byte) value;
			for (int i = 1; i < 8; i++) {
				value >>>= 8;
				obj.bin[offset--] = (byte) value;
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
			synchronized (entity) {
				this.internalSetLongD(field, offset, entity, this.internalGetLongD(offset, entity) + value);
			}
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			synchronized (entity) {
				this.internalSetLongD(field, offset, entity, (long) (this.internalGetLongD(offset, entity) + value));
			}
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private LongFieldAccessor() {
		super(8);
	}

	final static LongFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getLong(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getLongD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getLong(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getLongD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getLong(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getLongD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getLong(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getLongD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getLong(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getLongD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getLong(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getLongD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return unsafe.getLong(obj, (long) offset);
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getLong(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getLongD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getLong(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getLongD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getLong(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getLongD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getLong(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getLongD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getLong(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getLongD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getLong(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getLongD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		unsafe.putLong(obj, (long) offset, value);
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setLong(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setLongD(field, offset, obj, Convert.toLong(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setLong(field, offset, obj, value.getLong());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setLongD(field, offset, obj, value.getLong());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setLong(this.getLong(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setLong(this.getLongD(offset, dynObj));
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
		serializer.writeLong(this.getLong(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeLong(this.getLongD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setLong(field, offset, obj, deserializer.readLong());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setLongD(field, offset, dynObj, deserializer.readLong());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeLong(this.getLong(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeLong(this.getLongD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setLong(field, offset, value, unserializer.readLong());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setLongD(field, offset, value, unserializer.readLong());
	}

}
