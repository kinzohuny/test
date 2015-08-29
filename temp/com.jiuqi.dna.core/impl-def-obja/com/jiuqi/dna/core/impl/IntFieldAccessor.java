package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class IntFieldAccessor extends FieldAccessor {
	private static final IntFieldAccessor java = new IntFieldAccessor();

	private static final IntFieldAccessor dyn = new IntFieldAccessor() {
		@Override
		int getIntD(int offset, DynObj obj) {
			return unsafe.getInt(obj.bin, (long) offset);
		}

		@Override
		void setIntD(StructFieldDefineImpl field, int offset, DynObj obj,
				int value) {
			unsafe.putInt(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			long offsetL = offset;
			unsafe.putInt(dest.bin, offsetL, unsafe.getInt(src.bin, offsetL));
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			do {
				ov = unsafe.getIntVolatile(obj, of);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, ov + (int) value));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			long of = offset;
			Object obj = entity.bin;
			int ov;
			do {
				ov = unsafe.getIntVolatile(obj, of);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, ov + (int) value));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private static final IntFieldAccessor dynForIBM = new IntFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		int getIntD(int offset, DynObj obj) {
			synchronized (obj) {
				return this.internalGetIntD(offset, obj);
			}
		}

		private final int internalGetIntD(int offset, DynObj obj) {
			int v = obj.bin[offset++];
			for (int i = 1; i < 4; i++) {
				v = v << 8 | (obj.bin[offset++] & 0xFF);
			}
			return v;
		}

		@Override
		void setIntD(StructFieldDefineImpl field, int offset, DynObj obj,
				int value) {
			synchronized (obj) {
				this.internalSetIntD(field, offset, obj, value);
			}
		}

		private final void internalSetIntD(StructFieldDefineImpl field,
				int offset, DynObj obj, int value) {
			offset += 3;
			obj.bin[offset--] = (byte) value;
			for (int i = 1; i < 4; i++) {
				value >>>= 8;
				obj.bin[offset--] = (byte) value;
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
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			synchronized (entity) {
				this.internalSetIntD(field, offset, entity, (int) (this.internalGetIntD(offset, entity) + value));
			}
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			synchronized (entity) {
				this.internalSetIntD(field, offset, entity, (int) (this.internalGetIntD(offset, entity) + value));
			}
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private IntFieldAccessor() {
		super(4);
	}

	final static IntFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getInt(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getIntD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getInt(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getIntD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getInt(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getIntD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getInt(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getIntD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getInt(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getIntD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return unsafe.getInt(obj, (long) offset);
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return unsafe.getInt(obj, (long) offset);
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getInt(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getIntD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getInt(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getIntD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getInt(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getIntD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getInt(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getIntD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getInt(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getIntD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getInt(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getIntD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getInt(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getIntD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		unsafe.putInt(obj, (long) offset, value);
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		unsafe.putInt(obj, (long) offset, value);
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setInt(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setIntD(field, offset, obj, Convert.toInt(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setInt(field, offset, obj, value.getInt());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setIntD(field, offset, obj, value.getInt());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setInt(this.getInt(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setInt(this.getIntD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putInt(dest, offsetL, unsafe.getInt(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putInt(dest, offsetL, unsafe.getInt(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeInt(this.getInt(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeInt(this.getIntD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setInt(field, offset, obj, deserializer.readInt());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setInt(field, offset, dynObj, deserializer.readInt());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeInt(this.getInt(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeInt(this.getIntD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setInt(field, offset, value, unserializer.readInt());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setIntD(field, offset, value, unserializer.readInt());
	}

}
