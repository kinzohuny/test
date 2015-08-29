package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class ShortFieldAccessor extends FieldAccessor {

	private static final ShortFieldAccessor java = new ShortFieldAccessor();

	private static final ShortFieldAccessor dyn = new ShortFieldAccessor() {

		@Override
		short getShortD(int offset, DynObj obj) {
			return unsafe.getShort(obj.bin, (long) offset);
		}

		@Override
		void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
				short value) {
			unsafe.putShort(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			long offsetL = offset;
			unsafe.putShort(dest.bin, offsetL, unsafe.getShort(src.bin, offsetL));
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
				nv = ((ov + (int) value) & 0xffff) | (ov & 0xffff0000);
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
				nv = ((ov + (int) value) & 0xffff) | (ov & 0xffff0000);
			} while (!unsafe.compareAndSwapInt(obj, of, ov, nv));
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private static final ShortFieldAccessor dynForIBM = new ShortFieldAccessor() {

		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		short getShortD(int offset, DynObj obj) {
			synchronized (obj) {
				return this.internalGetShortD(offset, obj);
			}
		}

		private final short internalGetShortD(int offset, DynObj obj) {
			return (short) (obj.bin[offset] & 0xFF | (obj.bin[offset + 1] << 8));
		}

		@Override
		void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
				short value) {
			synchronized (obj) {
				this.internalSetShortD(field, offset, obj, value);
			}
		}

		private final void internalSetShortD(StructFieldDefineImpl field,
				int offset, DynObj obj, short value) {
			obj.bin[offset] = (byte) value;
			obj.bin[offset + 1] = (byte) (value >>> 8);
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			byte l, h;
			synchronized (src) {
				l = src.bin[offset];
				h = src.bin[offset + 1];
			}
			synchronized (dest) {
				dest.bin[offset] = l;
				dest.bin[offset + 1] = h;
			}
		}

		@Override
		final void SETLMergeLongValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, long value) {
			synchronized (entity) {
				this.internalSetShortD(field, offset, entity, (short) (this.internalGetShortD(offset, entity) + value));
			}
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			synchronized (entity) {
				this.internalSetShortD(field, offset, entity, (short) (this.internalGetShortD(offset, entity) + value));
			}
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private ShortFieldAccessor() {
		super(2);
	}

	final static ShortFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getShort(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getShortD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return Convert.toChar(this.getShort(offset, obj));
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return Convert.toChar(this.getShortD(offset, obj));
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getShort(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getShortD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getShort(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getShortD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return unsafe.getShort(obj, (long) offset);
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return unsafe.getShort(obj, (long) offset);
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getShort(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getShortD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getShort(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getShortD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getShort(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getShortD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getShort(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getShortD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getShort(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getShortD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getShort(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getShortD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getShort(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getShortD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getShort(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getShortD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		unsafe.putShort(obj, (long) offset, value);
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		unsafe.putShort(obj, (long) offset, value);
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setShort(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setShortD(field, offset, obj, Convert.toShort(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setShort(field, offset, obj, value.getShort());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setShortD(field, offset, obj, value.getShort());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setShort(this.getShort(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setShort(this.getShortD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putShort(dest, offsetL, unsafe.getShort(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putShort(dest, offsetL, unsafe.getShort(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeShort(this.getShort(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeShort(this.getShortD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setShort(field, offset, obj, deserializer.readShort());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setShortD(field, offset, dynObj, deserializer.readShort());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeShort(this.getShort(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeShort(this.getShortD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setShort(field, offset, value, unserializer.readShort());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setShortD(field, offset, value, unserializer.readShort());
	}

}
