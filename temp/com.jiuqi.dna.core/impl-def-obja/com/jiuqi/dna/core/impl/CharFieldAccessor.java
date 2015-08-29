package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.WritableValue;

class CharFieldAccessor extends FieldAccessor {

	private static final CharFieldAccessor java = new CharFieldAccessor();

	private static final CharFieldAccessor dyn = new CharFieldAccessor() {

		@Override
		char getCharD(int offset, DynObj obj) {
			return unsafe.getChar(obj.bin, (long) offset);
		}

		@Override
		void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
				char value) {
			unsafe.putChar(obj.bin, (long) offset, value);
		}

		@Override
		void assignD(StructFieldDefineImpl field, int offset, DynObj src,
				DynObj dest, OBJAContext objaContext,
				DataTranslatorHelper<?, ?> dth) {
			final long of = offset;
			unsafe.putChar(dest.bin, of, unsafe.getChar(src.bin, of));
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

	private static final CharFieldAccessor dynForIBM = new CharFieldAccessor() {

		@Override
		final int getBinDynFieldOffset(int binSize) {
			return binSize;
		}

		@Override
		final char getCharD(int offset, DynObj obj) {
			synchronized (obj) {
				return this.internalGetCharD(offset, obj);
			}
		}

		private final char internalGetCharD(int offset, DynObj obj) {
			return (char) (obj.bin[offset] & 0xFF | (obj.bin[offset + 1] << 8));
		}

		@Override
		void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
				char value) {
			synchronized (obj) {
				this.internalSetCharD(field, offset, obj, value);
			}
		}

		private final void internalSetCharD(StructFieldDefineImpl field,
				int offset, DynObj obj, char value) {
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
				this.internalSetCharD(field, offset, entity, (char) (this.internalGetCharD(offset, entity) + value));
			}
		}

		@Override
		final void SETLMergeDoubleValueNoCheck(StructFieldDefineImpl field,
				int offset, DynObj entity, double value) {
			synchronized (entity) {
				this.internalSetCharD(field, offset, entity, (char) (this.internalGetCharD(offset, entity) + value));
			}
		}

		@Override
		final boolean SETLMergeNullNoCheck(int offset, DynObj entity) {
			return false;
		}
	};

	private CharFieldAccessor() {
		super(2);
	}

	final static CharFieldAccessor select(boolean isJavaField) {
		return isJavaField ? java : Unsf.jvm_ibm || SPARC ? dynForIBM : dyn;
	}

	@Override
	boolean getBoolean(int offset, Object obj) {
		return Convert.toBoolean(this.getChar(offset, obj));
	}

	@Override
	boolean getBooleanD(int offset, DynObj obj) {
		return Convert.toBoolean(this.getCharD(offset, obj));
	}

	@Override
	char getChar(int offset, Object obj) {
		return unsafe.getChar(obj, (long) offset);
	}

	@Override
	char getCharD(int offset, DynObj obj) {
		return unsafe.getChar(obj, (long) offset);
	}

	@Override
	byte getByte(int offset, Object obj) {
		return Convert.toByte(this.getChar(offset, obj));
	}

	@Override
	byte getByteD(int offset, DynObj obj) {
		return Convert.toByte(this.getCharD(offset, obj));
	}

	@Override
	byte[] getBytes(int offset, Object obj) {
		return Convert.toBytes(this.getChar(offset, obj));
	}

	@Override
	byte[] getBytesD(int offset, DynObj obj) {
		return Convert.toBytes(this.getCharD(offset, obj));
	}

	@Override
	short getShort(int offset, Object obj) {
		return Convert.toShort(this.getChar(offset, obj));
	}

	@Override
	short getShortD(int offset, DynObj obj) {
		return Convert.toShort(this.getCharD(offset, obj));
	}

	@Override
	int getInt(int offset, Object obj) {
		return Convert.toInt(this.getChar(offset, obj));
	}

	@Override
	int getIntD(int offset, DynObj obj) {
		return Convert.toInt(this.getCharD(offset, obj));
	}

	@Override
	long getLong(int offset, Object obj) {
		return Convert.toLong(this.getChar(offset, obj));
	}

	@Override
	long getLongD(int offset, DynObj obj) {
		return Convert.toLong(this.getCharD(offset, obj));
	}

	@Override
	float getFloat(int offset, Object obj) {
		return Convert.toFloat(this.getChar(offset, obj));
	}

	@Override
	float getFloatD(int offset, DynObj obj) {
		return Convert.toFloat(this.getCharD(offset, obj));
	}

	@Override
	double getDouble(int offset, Object obj) {
		return Convert.toDouble(this.getChar(offset, obj));
	}

	@Override
	double getDoubleD(int offset, DynObj obj) {
		return Convert.toDouble(this.getCharD(offset, obj));
	}

	@Override
	long getDate(int offset, Object obj) {
		return Convert.toDate(this.getChar(offset, obj));
	}

	@Override
	long getDateD(int offset, DynObj obj) {
		return Convert.toDate(this.getCharD(offset, obj));
	}

	@Override
	String getString(int offset, Object obj) {
		return Convert.toString(this.getChar(offset, obj));
	}

	@Override
	String getStringD(int offset, DynObj obj) {
		return Convert.toString(this.getCharD(offset, obj));
	}

	@Override
	GUID getGUID(int offset, Object obj) {
		return Convert.toGUID(this.getChar(offset, obj));
	}

	@Override
	GUID getGUIDD(int offset, DynObj obj) {
		return Convert.toGUID(this.getCharD(offset, obj));
	}

	@Override
	Object getObject(int offset, Object obj) {
		return Convert.toObject(this.getChar(offset, obj));
	}

	@Override
	Object getObjectD(int offset, DynObj obj) {
		return Convert.toObject(this.getCharD(offset, obj));
	}

	@Override
	void setBoolean(StructFieldDefineImpl field, int offset, Object obj,
			boolean value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setBooleanD(StructFieldDefineImpl field, int offset, DynObj obj,
			boolean value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setChar(StructFieldDefineImpl field, int offset, Object obj, char value) {
		unsafe.putChar(obj, (long) offset, value);
	}

	@Override
	void setCharD(StructFieldDefineImpl field, int offset, DynObj obj,
			char value) {
		unsafe.putChar(obj, (long) offset, value);
	}

	@Override
	void setByte(StructFieldDefineImpl field, int offset, Object obj, byte value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setByteD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setBytes(StructFieldDefineImpl field, int offset, Object obj,
			byte[] value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setBytesD(StructFieldDefineImpl field, int offset, DynObj obj,
			byte[] value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setShort(StructFieldDefineImpl field, int offset, Object obj,
			short value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setShortD(StructFieldDefineImpl field, int offset, DynObj obj,
			short value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setInt(StructFieldDefineImpl field, int offset, Object obj, int value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setIntD(StructFieldDefineImpl field, int offset, DynObj obj, int value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setLong(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setLongD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setFloat(StructFieldDefineImpl field, int offset, Object obj,
			float value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setFloatD(StructFieldDefineImpl field, int offset, DynObj obj,
			float value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setDouble(StructFieldDefineImpl field, int offset, Object obj,
			double value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setDoubleD(StructFieldDefineImpl field, int offset, DynObj obj,
			double value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setDate(StructFieldDefineImpl field, int offset, Object obj, long value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setDateD(StructFieldDefineImpl field, int offset, DynObj obj,
			long value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setString(StructFieldDefineImpl field, int offset, Object obj,
			String value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setStringD(StructFieldDefineImpl field, int offset, DynObj obj,
			String value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setGUID(StructFieldDefineImpl field, int offset, Object obj, GUID value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setGUIDD(StructFieldDefineImpl field, int offset, DynObj obj,
			GUID value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setObject(StructFieldDefineImpl field, int offset, Object obj,
			Object value) {
		this.setChar(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setObjectD(StructFieldDefineImpl field, int offset, DynObj obj,
			Object value) {
		this.setCharD(field, offset, obj, Convert.toChar(value));
	}

	@Override
	void setValue(StructFieldDefineImpl field, int offset, Object obj,
			ReadableValue value) {
		this.setChar(field, offset, obj, value.getChar());
	}

	@Override
	void setValueD(StructFieldDefineImpl field, int offset, DynObj obj,
			ReadableValue value) {
		this.setCharD(field, offset, obj, value.getChar());
	}

	@Override
	void assignTo(int offset, Object obj, WritableValue target) {
		target.setChar(this.getChar(offset, obj));
	}

	@Override
	void assignToD(int offset, DynObj dynObj, WritableValue target) {
		target.setChar(this.getCharD(offset, dynObj));
	}

	@Override
	final void assign(StructFieldDefineImpl field, int offset, Object src,
			Object dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putChar(dest, offsetL, unsafe.getChar(src, offsetL));
	}

	@Override
	void assignD(StructFieldDefineImpl field, int offset, DynObj src,
			DynObj dest, OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		long offsetL = offset;
		unsafe.putChar(dest, offsetL, unsafe.getChar(src, offsetL));
	}

	/*--------------------------------------------------------------------*/
	// Serialization
	/*--------------------------------------------------------------------*/
	@Override
	void writeOut(StructFieldDefineImpl field, int offset, Object obj,
			InternalSerializer serializer) throws IOException {
		serializer.writeChar(this.getChar(offset, obj));
	}

	@Override
	void writeOutD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalSerializer serializer) throws IOException {
		serializer.writeChar(this.getCharD(offset, dynObj));
	}

	@Override
	void readIn(StructFieldDefineImpl field, int offset, Object obj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setChar(field, offset, obj, deserializer.readChar());
	}

	@Override
	void readInD(StructFieldDefineImpl field, int offset, DynObj dynObj,
			InternalDeserializer deserializer) throws IOException,
			StructDefineNotFoundException {
		this.setCharD(field, offset, dynObj, deserializer.readChar());
	}

	// ////////////////////////////////////
	// / new io Serialization

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final Object value, int offset) {
		return serializer.writeChar(this.getChar(offset, value));
	}

	@Override
	final boolean nioSerialize(final NSerializer serializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset) {
		return serializer.writeChar(this.getCharD(offset, value));
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final Object value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setChar(field, offset, value, unserializer.readChar());
	}

	@Override
	final void nioUnserialize(final NUnserializer unserializer,
			final StructFieldDefineImpl field, final DynObj value,
			final int offset, final Object hint, DataTranslatorHelper<?, ?> dth) {
		this.setCharD(field, offset, value, unserializer.readChar());
	}

}
