package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.exception.NumericOverflowException;
import com.jiuqi.dna.core.spi.application.NumericOverflowOptionTask;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class DoubleFieldAccessor extends FieldAccessor {
	/**
	 * 模式
	 */
	public static int numeric_overflow_mode = NumericOverflowOptionTask.SET_NULL_PRINT_ERROR;

	/**
	 * 调整精度
	 * 
	 * @return
	 */
	final static double checkNumericOverflow(StructFieldDefineImpl field,
			double value, Object obj) {
		final DataType dt = field.type;
		if (dt.getClass() == NumericDBType.class) {
			final NumericDBType ndbt = (NumericDBType) dt;
			if (value < ndbt.min || ndbt.max < value) {
				switch (numeric_overflow_mode) {
				case NumericOverflowOptionTask.SET_NULL_PRINT_ERROR:
					System.err.println(NumericOverflowException.formatMessage(ndbt.precision, ndbt.scale, value, field.name));
				case NumericOverflowOptionTask.SET_NULL:
					if (obj instanceof DynObj) {
						field.setFieldValueNull(obj);
					}
					return 0.0;
				default:
					throw new NumericOverflowException(ndbt.precision, ndbt.scale, value, field.name);
				}
			}
		}
		return value;
	}

	private static final DoubleFieldAccessor java = new DoubleFieldAccessor();
	private static final DoubleFieldAccessor dyn = new DoubleFieldAccessor() {
		@Override
		double getDoubleD(int offset, DynObj obj) {
			return unsafe.getDouble(obj.bin, (long) offset);
		}

		@Override
		void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
				double value) {
			unsafe.putDouble(obj.bin, (long) offset, checkNumericOverflow(field, value, obj));
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			final long offsetL = offset;
			unsafe.putDouble(dest.bin, offsetL, unsafe.getDouble(src.bin, offsetL));
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			final long of = offset;
			final Object obj = entity.bin;
			long ov;
			long nv;
			do {
				ov = unsafe.getLongVolatile(obj, of);
				nv = Double.doubleToRawLongBits(Double.longBitsToDouble(ov) + value);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, nv));
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			final long of = offset;
			final Object obj = entity.bin;
			long ov;
			long nv;
			do {
				ov = unsafe.getLongVolatile(obj, of);
				nv = Double.doubleToLongBits(Double.longBitsToDouble(ov) + value);
			} while (!unsafe.compareAndSwapLong(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};
	private static final DoubleFieldAccessor dynForIBM = new DoubleFieldAccessor() {
		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		double getDoubleD(int offset, DynObj obj) {
			synchronized (obj) {
				return this.internalGetDoubleD(offset, obj);
			}
		}

		private final double internalGetDoubleD(int offset, DynObj obj) {
			long v = obj.bin[offset++];
			for (int i = 1; i < 8; i++) {
				v = v << 8 | (obj.bin[offset++] & 0xFF);
			}
			return Double.longBitsToDouble(v);
		}

		@Override
		void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
				double value) {
			synchronized (obj) {
				this.internalSetDoubleD(field, offset, obj, value);
			}
		}

		private final void internalSetDoubleD(StructFieldDefineImpl field,
				int offset, DynObj obj, double value) {
			long v = Double.doubleToRawLongBits(checkNumericOverflow(field, value, obj));
			offset += 7;
			obj.bin[offset--] = (byte) v;
			for (int i = 1; i < 8; i++) {
				v >>>= 8;
				obj.bin[offset--] = (byte) v;
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
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			synchronized (entity) {
				this.internalSetDoubleD(field, offset, entity, this.internalGetDoubleD(offset, entity) + value);
			}
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			synchronized (entity) {
				this.internalSetDoubleD(field, offset, entity, this.internalGetDoubleD(offset, entity) + value);
			}
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private DoubleFieldAccessor() {
		super(8);
	}

	final static DoubleFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getDouble(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getDoubleD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getDouble(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getDoubleD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getDouble(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getDoubleD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getDouble(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getDoubleD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getDouble(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getDoubleD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getDouble(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getDoubleD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getDouble(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getDoubleD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getDouble(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getDoubleD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return unsafe.getDouble(obj, (long) offset);
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return unsafe.getDouble(obj, (long) offset);
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getDouble(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getDoubleD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getDouble(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getDoubleD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getDouble(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getDoubleD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getDouble(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getDoubleD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		unsafe.putDouble(obj, (long) offset, checkNumericOverflow(field, value, obj));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		unsafe.putDouble(obj, (long) offset, checkNumericOverflow(field, value, obj));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setDouble(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setDoubleD(field, offset, obj, Convert.toDouble(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setDouble(field, offset, obj, value.getDouble());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setDoubleD(field, offset, obj, value.getDouble());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setDouble(this.getDouble(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setDouble(this.getDoubleD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putDouble(dest, offsetL, unsafe.getDouble(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putDouble(dest, offsetL, unsafe.getDouble(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeDouble(this.getDouble(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeDouble(this.getDoubleD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDouble(field, offset, obj, deserializer.readDouble());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setDoubleD(field, offset, dynObj, deserializer.readDouble());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeDouble(this.getDouble(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeDouble(this.getDoubleD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setDouble(field, offset, value, unserializer.readDouble());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setDoubleD(field, offset, value, unserializer.readDouble());
	}

}
