package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.StructFieldDefineImpl.LoopStructFieldDefine;
import com.jiuqi.dna.core.type.DataType;

public interface NSerializeBase_1_0 {
	// 1.0
	public final static short SERIALIZE_VERSION = 0x0100;

	static final Object NONE_OBJECT = new Object();

	// ===========================================================================
	// 数据长度定义
	static final byte SIZE_BOOLEAN = 1;
	static final byte SIZE_BYTE = 1;
	static final byte SIZE_SHORT = 2;
	static final byte SIZE_CHAR = 2;
	static final byte SIZE_INT = 4;
	static final byte SIZE_FLOAT = 4;
	static final byte SIZE_LONG = 8;
	static final byte SIZE_DOUBLE = 8;
	static final byte SIZE_GUID = SIZE_LONG + SIZE_LONG;
	static final byte SIZE_DATE = SIZE_LONG;
	static final byte SIZE_HEAD = SIZE_BYTE;
	static final byte SIZE_POINTER = SIZE_INT;

	// ===========================================================================

	static final byte SORTCODE_MASK = (byte) (0x07 << 5);
	static final byte SORTCODE_DEMASK = ~SORTCODE_MASK;

	static final int SMALLENUM_MAX_ORDINAL = Byte.MAX_VALUE;
	static final int LARGEENUM_MAX_ORDINAL = Integer.MAX_VALUE;
	static final int CONTINUOUS_BOOLEAN_MAX_COUNT = 8;
	static final int CONTINUOUS_NULL_MAX_COUNT = 0xFF & SORTCODE_DEMASK;

	// ===========================================================================
	/**
	 * <pre>
	 *   X       X       X       X       X       X       X       X
	 *  |---一级分类编码---|     |-----------二级分类编码-----------|
	 * </pre>
	 */

	static final byte HEAD_MARK_POINTER = MarkObject.POINTER.headCode;
	static final byte HEAD_MARK_UNSERIALIZABLE = MarkObject.UNSERIALIZABLE.headCode;
	static final byte HEAD_FIXLEN_GUID = FixLengthObject.GUID.headCode;
	static final byte HEAD_FIXLEN_BOOLEAN_OBJECT_TRUE = FixLengthObject.BOOLEAN_OBJECT_TRUE.headCode;
	static final byte HEAD_FIXLEN_BOOLEAN_OBJECT_FALSE = FixLengthObject.BOOLEAN_OBJECT_FALSE.headCode;
	static final byte HEAD_FIXLEN_BYTE_OBJECT = FixLengthObject.BYTE_OBJECT.headCode;
	static final byte HEAD_FIXLEN_CHAR_OBJECT = FixLengthObject.CHAR_OBJECT.headCode;
	static final byte HEAD_FIXLEN_SHORT_OBJECT = FixLengthObject.SHORT_OBJECT.headCode;
	static final byte HEAD_FIXLEN_INT_OBJECT = FixLengthObject.INT_OBJECT.headCode;
	static final byte HEAD_FIXLEN_FLOAT_OBJECT = FixLengthObject.FLOAT_OBJECT.headCode;
	static final byte HEAD_FIXLEN_LONG_OBJECT = FixLengthObject.LONG_OBJECT.headCode;
	static final byte HEAD_FIXLEN_DOUBLE_OBJECT = FixLengthObject.DOUBLE_OBJECT.headCode;
	static final byte HEAD_FIXLEN_EMPTY_GUID = FixLengthObject.EMPTY_GUID.headCode;
	static final byte HEAD_FIXLEN_EMPTY_STRING = FixLengthObject.EMPTY_STRING.headCode;
	static final byte HEAD_FIXLEN_EMPTY_BOOLEANARRAY = FixLengthObject.EMPTY_BOOLEANARRAY.headCode;
	static final byte HEAD_FIXLEN_EMPTY_BYTEARRAY = FixLengthObject.EMPTY_BYTEARRAY.headCode;
	static final byte HEAD_FIXLEN_EMPTY_CHARARRAY = FixLengthObject.EMPTY_CHARARRAY.headCode;
	static final byte HEAD_FIXLEN_EMPTY_SHORTARRAY = FixLengthObject.EMPTY_SHORTARRAY.headCode;
	static final byte HEAD_FIXLEN_EMPTY_INTARRAY = FixLengthObject.EMPTY_INTARRAY.headCode;
	static final byte HEAD_FIXLEN_EMPTY_FLOATARRAY = FixLengthObject.EMPTY_FLOATARRAY.headCode;
	static final byte HEAD_FIXLEN_EMPTY_LONGARRAY = FixLengthObject.EMPTY_LONGARRAY.headCode;
	static final byte HEAD_FIXLEN_EMPTY_DOUBLEARRAY = FixLengthObject.EMPTY_DOUBLEARRAY.headCode;
	static final byte HEAD_FIXLEN_STRUCT_DECLARED = FixLengthObject.STRUCT_DECLARED.headCode;
	static final byte HEAD_FIXLEN_SMALLENUM_DECLARED = FixLengthObject.SMALLENUM_DECLARED.headCode;
	static final byte HEAD_FIXLEN_LARGEENUM_DECLARED = FixLengthObject.LARGEENUM_DECLARED.headCode;
	static final byte HEAD_VARLEN_BOOLEANARRAY = VarLengthObject.BOOLEANARRAY.headCode;
	static final byte HEAD_VARLEN_BYTEARRAY = VarLengthObject.BYTEARRAY.headCode;
	static final byte HEAD_VARLEN_CHARARRAY = VarLengthObject.CHARARRAY.headCode;
	static final byte HEAD_VARLEN_SHORTARRAY = VarLengthObject.SHORTARRAY.headCode;
	static final byte HEAD_VARLEN_INTARRAY = VarLengthObject.INTARRAY.headCode;
	static final byte HEAD_VARLEN_FLOATARRAY = VarLengthObject.FLOATARRAY.headCode;
	static final byte HEAD_VARLEN_LONGARRAY = VarLengthObject.LONGARRAY.headCode;
	static final byte HEAD_VARLEN_DOUBLEARRAY = VarLengthObject.DOUBLEARRAY.headCode;
	static final byte HEAD_VARLEN_STRING = VarLengthObject.STRING.headCode;
	static final byte HEAD_WITHTYPEID_TYPE_POINTER = WithTypeIDObject.TYPE_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_TYPE_GUID = WithTypeIDObject.TYPE_GUID.headCode;
	static final byte HEAD_WITHTYPEID_CLASS_POINTER = WithTypeIDObject.CLASS_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_CLASS_GUID = WithTypeIDObject.CLASS_GUID.headCode;
	static final byte HEAD_WITHTYPEID_SMALLENUM_POINTER = WithTypeIDObject.SMALLENUM_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_SMALLENUM_GUID = WithTypeIDObject.SMALLENUM_GUID.headCode;
	static final byte HEAD_WITHTYPEID_LARGEENUM_POINTER = WithTypeIDObject.LARGEENUM_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_LARGEENUM_GUID = WithTypeIDObject.LARGEENUM_GUID.headCode;
	static final byte HEAD_WITHTYPEID_STRUCT_POINTER = WithTypeIDObject.STRUCT_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_STRUCT_GUID = WithTypeIDObject.STRUCT_GUID.headCode;
	static final byte HEAD_WITHTYPEID_CUSTOM_POINTER = WithTypeIDObject.CUSTOM_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_CUSTOM_GUID = WithTypeIDObject.CUSTOM_GUID.headCode;
	static final byte HEAD_WITHTYPEID_OBJECTARRAY_POINTER = WithTypeIDObject.OBJECTARRAY_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_OBJECTARRAY_GUID = WithTypeIDObject.OBJECTARRAY_GUID.headCode;
	static final byte HEAD_WITHTYPEID_EMPTYOBJECTARRAY_POINTER = WithTypeIDObject.EMPTY_OBJECTARRAY_POINTER.headCode;
	static final byte HEAD_WITHTYPEID_EMPTYOBJECTARRAY_GUID = WithTypeIDObject.EMPTY_OBJECTARRAY_GUID.headCode;

	static interface ObjectReader {

		Object read(NUnserializer_1_0 unserializer, DataType declareType,
				Object hint);

	}

	static enum ObjectSortCode {

		NULL {
			@Override
			final ObjectReader[] getReaders() {
				return NullObjectReader.READERS;
			}
		},

		MARK {
			@Override
			final ObjectReader[] getReaders() {
				return MarkObject.values();
			}
		},

		FIX_LENGTH {
			@Override
			final ObjectReader[] getReaders() {
				return FixLengthObject.values();
			}
		},

		VAR_LENGTH {
			@Override
			final ObjectReader[] getReaders() {
				return VarLengthObject.values();
			}
		},

		WITH_TYPEID {
			@Override
			final ObjectReader[] getReaders() {
				return WithTypeIDObject.values();
			}
		};

		abstract ObjectReader[] getReaders();

	}

	static enum MarkObject implements ObjectReader {

		POINTER {

			public final Object read(NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readPointer();
			}

		},

		UNSERIALIZABLE {

			public final Object read(NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readUnserializable(hint);
			}

		};

		final byte headCode;

		private MarkObject() {
			this.headCode = (byte) ((ObjectSortCode.MARK.ordinal() << 5) | (this.ordinal()));
		}

	}

	static enum FixLengthObject implements ObjectReader {

		GUID {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readGUIDValue();
			}

		},

		BOOLEAN_OBJECT_TRUE {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readBooleanObject(true);
			}

		},

		BOOLEAN_OBJECT_FALSE {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readBooleanObject(false);
			}

		},

		BYTE_OBJECT {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readByteObject();
			}

		},

		CHAR_OBJECT {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readCharObject();
			}

		},

		SHORT_OBJECT {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readShortObject();
			}

		},

		INT_OBJECT {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readIntObject();
			}

		},

		FLOAT_OBJECT {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readFloatObject();
			}

		},

		LONG_OBJECT {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readLongObject();
			}

		},

		DOUBLE_OBJECT {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readDoubleObject();
			}

		},

		EMPTY_GUID {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyGUID();
			}

		},

		EMPTY_STRING {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyString();
			}

		},

		EMPTY_BOOLEANARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyBooleanArray();
			}

		},

		EMPTY_BYTEARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyByteArray();
			}

		},

		EMPTY_CHARARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyCharArray();
			}

		},

		EMPTY_SHORTARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyShortArray();
			}

		},

		EMPTY_INTARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyIntArray();
			}

		},

		EMPTY_FLOATARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyFloatArray();
			}

		},

		EMPTY_LONGARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyLongArray();
			}

		},

		EMPTY_DOUBLEARRAY {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyDoubleArray();
			}

		},

		STRUCT_DECLARED {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readStruct(declareType, hint);
			}

		},

		SMALLENUM_DECLARED {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readSmallEnum(declareType);
			}

		},

		LARGEENUM_DECLARED {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readLargeEnum(declareType);
			}

		};

		final byte headCode;

		private FixLengthObject() {
			this.headCode = (byte) ((ObjectSortCode.FIX_LENGTH.ordinal() << 5) | (this.ordinal()));
		}

	}

	static enum VarLengthObject implements ObjectReader {

		BOOLEANARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, int startIndex, final int length) {
				final boolean[] array = (boolean[]) data;
				while (startIndex < length && dataOutputFragment.remain() > 0) {
					int writeCount = length - startIndex;
					writeCount = writeCount > 8 ? 8 : writeCount;
					int index = startIndex;
					startIndex += writeCount;
					byte booleans = 0;
					for (int offset = 0; index < startIndex;) {
						if (array[index++]) {
							booleans |= (1 << offset);
						}
						offset++;
					}
					dataOutputFragment.writeByte(booleans);
				}
				return startIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new boolean[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final boolean[] array = (boolean[]) data;
				final int bufferRemain = dataInputFragment.remain() * 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = unserializer.readBoolean();
				}
				return canWriteToIndex;
			}

		},

		BYTEARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final byte[] array = (byte[]) data;
				final int bufferRemain = dataOutputFragment.remain();
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeByte(array[index]);
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new byte[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final byte[] array = (byte[]) data;
				final int bufferRemain = dataInputFragment.remain();
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readByte();
				}
				return canWriteToIndex;
			}

		},

		SHORTARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final short[] array = (short[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeShort(array[index]);
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new short[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final short[] array = (short[]) data;
				final int bufferRemain = dataInputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readShort();
				}
				return canWriteToIndex;
			}

		},

		CHARARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final char[] array = (char[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeChar(array[index]);
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new char[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final char[] array = (char[]) data;
				final int bufferRemain = dataInputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readChar();
				}
				return canWriteToIndex;
			}

		},

		INTARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final int[] array = (int[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeInt(array[index]);
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(int VLDLength) {
				return new int[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final int[] array = (int[]) data;
				final int bufferRemain = dataInputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readInt();
				}
				return canWriteToIndex;
			}

		},

		FLOATARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final float[] array = (float[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeFloat(array[index]);
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new float[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final float[] array = (float[]) data;
				final int bufferRemain = dataInputFragment.remain() / 4;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readFloat();
				}
				return canWriteToIndex;
			}

		},

		LONGARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final long[] array = (long[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeLong(array[index]);
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new long[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final long[] array = (long[]) data;
				final int bufferRemain = dataInputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readLong();
				}
				return canWriteToIndex;
			}

		},

		DOUBLEARRAY {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final double[] array = (double[]) data;
				final int bufferRemain = dataOutputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeDouble(array[index]);
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new double[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final double[] array = (double[]) data;
				final int bufferRemain = dataInputFragment.remain() / 8;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readDouble();
				}
				return canWriteToIndex;
			}

		},

		STRING {

			@Override
			final int writeElement(final DataOutputFragment dataOutputFragment,
					final Object data, final int startIndex, final int length) {
				final String string = (String) data;
				final int bufferRemain = dataOutputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					dataOutputFragment.writeChar(string.charAt(index));
				}
				return canWriteToIndex;
			}

			@Override
			final Object newVLDObject(final int VLDLength) {
				return new char[VLDLength];
			}

			@Override
			final int readElement(final NUnserializer unserializer,
					final DataInputFragment dataInputFragment, Object data,
					final int startIndex, final int length) {
				final char[] array = (char[]) data;
				final int bufferRemain = dataInputFragment.remain() / 2;
				final int canWriteToIndex = length - startIndex > bufferRemain ? startIndex + bufferRemain : length;
				for (int index = startIndex; index < canWriteToIndex; index++) {
					array[index] = dataInputFragment.readChar();
				}
				return canWriteToIndex;
			}

		};

		final byte headCode;

		public final Object read(final NUnserializer_1_0 unserializer,
				final DataType declareType, final Object hint) {
			return unserializer.readVarLengthObject(this);
		}

		/**
		 * @return 返回写了多少个元素
		 */
		abstract int writeElement(final DataOutputFragment dataOutputFragment,
				final Object data, final int startIndex, final int length);

		abstract Object newVLDObject(final int VLDLength);

		/**
		 * @return 返回读取了多少个元素
		 */
		abstract int readElement(final NUnserializer unserializer,
				final DataInputFragment dataInputFragment, Object data,
				final int startIndex, final int length);

		private VarLengthObject() {
			this.headCode = (byte) ((ObjectSortCode.VAR_LENGTH.ordinal() << 5) | (this.ordinal()));
		}

	}

	static enum WithTypeIDObject implements ObjectReader {

		TYPE_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readDataTypeByPointer();
			}

		},

		TYPE_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readDataTypeByGUID();
			}

		},

		CLASS_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readClassByPointer();
			}

		},

		CLASS_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readClassByGUID();
			}

		},

		SMALLENUM_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readSmallEnumByPointer();
			}

		},

		SMALLENUM_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readSmallEnumByGUID();
			}

		},

		LARGEENUM_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readLargeEnumByPointer();
			}

		},

		LARGEENUM_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readLargeEnumByGUID();
			}

		},

		STRUCT_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readStructByPointer(hint);
			}

		},

		STRUCT_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readStructByGUID(hint);
			}

		},

		CUSTOM_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readCustomByPointer(hint);
			}

		},

		CUSTOM_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readCustomByGUID(hint);
			}

		},

		OBJECTARRAY_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readObjectArrayByPointer(hint);
			}

		},

		OBJECTARRAY_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readObjectArrayByGUID(hint);
			}

		},

		EMPTY_OBJECTARRAY_POINTER(true) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyObjectArrayByPointer(hint);
			}

		},

		EMPTY_OBJECTARRAY_GUID(false) {

			public final Object read(final NUnserializer_1_0 unserializer,
					final DataType declareType, final Object hint) {
				return unserializer.readEmptyObjectArrayByGUID(hint);
			}

		};

		final byte headCode;

		final boolean byPointer;

		private WithTypeIDObject(final boolean byPointer) {
			this.byPointer = byPointer;
			this.headCode = (byte) ((ObjectSortCode.WITH_TYPEID.ordinal() << 5) | (this.ordinal()));
		}

	}

	static final class NullObjectReader implements ObjectReader {

		private NullObjectReader(final byte index) {
			this.index = index;
		}

		public final Object read(final NUnserializer_1_0 unserializer,
				final DataType declareType, final Object hint) {
			return unserializer.readNull(this.index);
		}

		private final byte index;

		static final NullObjectReader[] READERS;

		static {
			READERS = new NullObjectReader[CONTINUOUS_NULL_MAX_COUNT + 1];
			for (int index = 1; index <= CONTINUOUS_NULL_MAX_COUNT;) {
				READERS[index] = new NullObjectReader((byte) (index++));
			}
		}

	}

	static final class ObjectReaderContainer {

		private ObjectReaderContainer() {

		}

		static final ObjectReader[][] OBJECT_READERS;

		static {
			final ObjectSortCode[] sortCodes = ObjectSortCode.values();
			final int size = sortCodes.length;
			OBJECT_READERS = new ObjectReader[size][];
			for (int index = 0; index < size; index++) {
				OBJECT_READERS[index] = sortCodes[index].getReaders();
			}
		}

	}

	// ===========================================================================
	static enum ContinuousState {

		NONE,

		NULL,

		BOOLEAN

	}

	static final class ObjectArrayFieldDefine extends LoopStructFieldDefine {

		private ObjectArrayFieldDefine() {
			super("ObjectArray");
		}

		@Override
		final boolean serialize(final NSerializer serializer,
				final Object object) {
			return serializer.writeObjectArrayElement(object);
		}

		@Override
		final void unserialize(final NUnserializer unserializer,
				final Object object, final Object hint,
				DataTranslatorHelper<?, ?> dth) {
			unserializer.readObjectArrayElement(hint, dth);
		}

		static final ObjectArrayFieldDefine INSTANCE = new ObjectArrayFieldDefine();

	}

	static final class CustomFieldDefine extends LoopStructFieldDefine {

		private CustomFieldDefine() {
			super("CustomObject");
		}

		@Override
		final boolean serialize(final NSerializer serializer,
				final Object object) {
			return serializer.writeCustomData(object);
		}

		@Override
		final void unserialize(final NUnserializer unserializer,
				final Object object, final Object hint,
				DataTranslatorHelper<?, ?> dth) {
			unserializer.readCustomObject(hint);
		}

		static final CustomFieldDefine INSTANCE = new CustomFieldDefine();

	}

}
