package com.jiuqi.dna.core.impl;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.DataObjectTranslator;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;

public class NSerializer_1_0 extends NSerializer implements NSerializeBase_1_0 {

	public static final NSerializerFactory FACTORY = new NSerializerFactory(SERIALIZE_VERSION) {

		@Override
		public final NSerializer newNSerializer() {
			return new NSerializer_1_0();
		}

	};

	public NSerializer_1_0() {
		this.stack = new Stack();
		this.objectIndexMap = new ObjectIndexMap();
		this.rootObject = NONE_OBJECT;
		this.CState = ContinuousState.NONE;
	}

	@Override
	public short getVersion() {
		return NSerializeBase_1_0.SERIALIZE_VERSION;
	}

	@Override
	public final boolean serializeStart(final Object object,
			final DataOutputFragment fragment) {
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		if (!this.isSerialized()) {
			throw new IllegalStateException("当前序列化任务还未完成");
		}
		this.dataOutputFragment = fragment;
		try {
			this.rootObject = object;
			if (this.writeObject(object, null) && (this.stack.isEmpty() || this.processStack())) {
				return this.tryFinishSerialize();
			}
			return false;
		} finally {
			this.dataOutputFragment = null;
		}
	}

	@Override
	public final boolean serializeStart(final Object object,
			final DataOutputFragment fragment, final boolean fully) {
		if (fully) {
			throw new UnsupportedOperationException("当前版本的序列化器不支持完全模式的序列化。" + this.getVersion());
		} else {
			return this.serializeStart(object, fragment);
		}
	}

	@Override
	public final boolean serializeRest(final DataOutputFragment fragment) {
		if (this.isSerialized()) {
			throw new IllegalStateException("当前序列化任务已经完成");
		}
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		this.dataOutputFragment = fragment;
		try {
			if (this.stack.isEmpty()) {
				if (this.CState == ContinuousState.NONE) {
					if (this.writeObject(this.rootObject, null) && (this.stack.isEmpty() || this.processStack())) {
						return this.tryFinishSerialize();
					}
				} else {
					return this.tryFinishSerialize();
				}
			} else {
				if (this.processStack()) {
					return this.tryFinishSerialize();
				}
			}
			return false;
		} finally {
			this.dataOutputFragment = null;
		}
	}

	@Override
	public final boolean isSerialized() {
		return this.rootObject == NONE_OBJECT;
	}

	@Override
	public final void reset() {
		this.dataOutputFragment = null;
		this.rootObject = NONE_OBJECT;
		this.stack.reset();
		this.objectIndexMap.reset();
		this.CState = ContinuousState.NONE;
		this.CValue = 0;
		this.CCount = 0;
		this.VLOWriter = null;
		this.VLOObject = null;
		this.VLOLength = 0;
		this.VLOWrote = 0;
	}

	@Override
	final boolean writeObject(final Object value, final DataType declaredType) {
		if (value == null) {
			return this.writeNullObject();
		} else {
			if (this.tryFinishWriteC()) {
				final int valueIndex = this.objectIndexMap.tryGetIndex(value);
				if (valueIndex < 0) {
					if (value instanceof DataType) {
						return this.writeDataType((DataType) value);
					} else {
						return ((ObjectDataTypeInternal) DataTypeBase.dataTypeOfJavaObj(value)).nioSerializeData(this, value);
					}
				} else {
					return this.writePointer(valueIndex);
				}
			} else {
				return SERIALIZE_FAIL;
			}
		}
	}

	@Override
	final boolean writeBoolean(final boolean value) {
		switch (this.CState) {
		case BOOLEAN:
			if (this.CCount < CONTINUOUS_BOOLEAN_MAX_COUNT) {
				if (value) {
					this.CValue |= (1 << this.CCount);
				}
				this.CCount++;
				return SERIALIZE_SUCCESS;
			}
		case NULL:
			if (!this.tryFinishWriteC()) {
				return SERIALIZE_FAIL;
			}
		}
		this.CState = ContinuousState.BOOLEAN;
		this.CValue = value ? (byte) 0x01 : 0;
		this.CCount = 1;
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeByte(final byte value) {
		if (this.dataOutputFragment.remain() < SIZE_BYTE) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeChar(final char value) {
		if (this.dataOutputFragment.remain() < SIZE_CHAR) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeChar(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeDouble(final double value) {
		if (this.dataOutputFragment.remain() < SIZE_DOUBLE) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeDouble(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeFloat(final float value) {
		if (this.dataOutputFragment.remain() < SIZE_FLOAT) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeFloat(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeInt(final int value) {
		if (this.dataOutputFragment.remain() < SIZE_INT) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeInt(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeLong(final long value) {
		if (this.dataOutputFragment.remain() < SIZE_LONG) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeLong(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeShort(final short value) {
		if (this.dataOutputFragment.remain() < SIZE_SHORT) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeShort(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeBooleanObject(final Boolean value) {
		final int objectIndex = this.objectIndexMap.tryGetIndex(value);
		if (objectIndex < 0) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(value ? HEAD_FIXLEN_BOOLEAN_OBJECT_TRUE : HEAD_FIXLEN_BOOLEAN_OBJECT_FALSE);
				this.objectIndexMap.tryPutObject(value);
				return SERIALIZE_SUCCESS;
			}
		} else {
			return this.writePointer(objectIndex);
		}
	}

	@Override
	final boolean writeByteObject(final Byte value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_BYTE) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_FIXLEN_BYTE_OBJECT);
			this.dataOutputFragment.writeByte(value);
			this.objectIndexMap.tryPutObject(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeCharObject(final Character value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_CHAR) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_FIXLEN_CHAR_OBJECT);
			this.dataOutputFragment.writeChar(value);
			this.objectIndexMap.tryPutObject(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeDoubleObject(final Double value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_DOUBLE) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_FIXLEN_DOUBLE_OBJECT);
			this.dataOutputFragment.writeDouble(value);
			this.objectIndexMap.tryPutObject(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeFloatObject(final Float value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_FLOAT) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_FIXLEN_FLOAT_OBJECT);
			this.dataOutputFragment.writeFloat(value);
			this.objectIndexMap.tryPutObject(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeIntObject(final Integer value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_FIXLEN_INT_OBJECT);
			this.dataOutputFragment.writeInt(value);
			this.objectIndexMap.tryPutObject(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeLongObject(final Long value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_LONG) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_FIXLEN_LONG_OBJECT);
			this.dataOutputFragment.writeLong(value);
			this.objectIndexMap.tryPutObject(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeShortObject(final Short value) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_SHORT) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_FIXLEN_SHORT_OBJECT);
			this.dataOutputFragment.writeShort(value);
			this.objectIndexMap.tryPutObject(value);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeByteArrayField(final byte[] value) {
		if (value == null) {
			return this.writeNullObject();
		} else {
			if (this.tryFinishWriteC()) {
				final int objectIndex = this.objectIndexMap.tryGetIndex(value);
				if (objectIndex < 0) {
					return this.writeByteArrayData(value);
				} else {
					return this.writePointer(objectIndex);
				}
			} else {
				return SERIALIZE_FAIL;
			}
		}
	}

	@Override
	final boolean writeDateField(final long value) {
		return this.writeLong(value);
	}

	@Override
	final boolean writeEnumField(final Enum<?> value,
			final EnumTypeImpl<?> enumType) {
		if (value == null) {
			return this.writeNullObject();
		} else {
			if (this.tryFinishWriteC()) {
				final int objectIndex = this.objectIndexMap.tryGetIndex(value);
				if (objectIndex < 0) {
					return this.writeEnumData(value, enumType, value.getClass() == enumType.javaClass);
				} else {
					return this.writePointer(objectIndex);
				}
			} else {
				return SERIALIZE_FAIL;
			}
		}
	}

	@Override
	final boolean writeGUIDField(final GUID value) {
		if (value == null) {
			return this.writeNullObject();
		} else {
			if (this.tryFinishWriteC()) {
				final int objectIndex = this.objectIndexMap.tryGetIndex(value);
				if (objectIndex < 0) {
					return this.writeGUIDData(value);
				} else {
					return this.writePointer(objectIndex);
				}
			} else {
				return SERIALIZE_FAIL;
			}
		}
	}

	@Override
	final boolean writeStringField(final String value) {
		if (value == null) {
			return this.writeNullObject();
		} else {
			if (this.tryFinishWriteC()) {
				final int objectIndex = this.objectIndexMap.tryGetIndex(value);
				if (objectIndex < 0) {
					return this.writeStringData(value);
				} else {
					return this.writePointer(objectIndex);
				}
			} else {
				return SERIALIZE_FAIL;
			}
		}
	}

	@Override
	final boolean writeUnserializable() {
		if (this.dataOutputFragment.remain() < SIZE_HEAD || !this.tryFinishWriteC()) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_MARK_UNSERIALIZABLE);
			return SERIALIZE_SUCCESS;
		}
	}

	@Override
	final boolean writeBooleanArrayData(final boolean[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_BOOLEANARRAY, VarLengthObject.BOOLEANARRAY, value, value.length);
	}

	@Override
	final boolean writeByteArrayData(final byte[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_BYTEARRAY, VarLengthObject.BYTEARRAY, value, value.length);
	}

	@Override
	final boolean writeCharArrayData(final char[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_CHARARRAY, VarLengthObject.CHARARRAY, value, value.length);
	}

	@Override
	final boolean writeFloatArrayData(final float[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_FLOATARRAY, VarLengthObject.FLOATARRAY, value, value.length);
	}

	@Override
	final boolean writeIntArrayData(final int[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_INTARRAY, VarLengthObject.INTARRAY, value, value.length);
	}

	@Override
	final boolean writeLongArrayData(final long[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_LONGARRAY, VarLengthObject.LONGARRAY, value, value.length);
	}

	@Override
	final boolean writeShortArrayData(final short[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_SHORTARRAY, VarLengthObject.SHORTARRAY, value, value.length);
	}

	@Override
	final boolean writeDoubleArrayData(final double[] value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_DOUBLEARRAY, VarLengthObject.DOUBLEARRAY, value, value.length);
	}

	@Override
	final boolean writeStringData(final String value) {
		return this.writeVarLengthObject(FixLengthObject.EMPTY_STRING, VarLengthObject.STRING, value, value.length());
	}

	@Override
	final boolean writeGUIDData(final GUID value) {
		if (value.equals(GUID.emptyID)) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_FIXLEN_EMPTY_GUID);
			}
		} else {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_FIXLEN_GUID);
				this.dataOutputFragment.writeLong(value.getMostSigBits());
				this.dataOutputFragment.writeLong(value.getLeastSigBits());
			}
		}
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeClassData(final Class<?> value) {
		final GUID typeID = DataTypeBase.dataTypeOfJavaClass(value).getID();
		final int IDIndex = this.objectIndexMap.tryGetIndex(typeID);
		if (IDIndex < 0) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_CLASS_GUID);
				this.forceWriteGUIDValue(typeID);
			}
		} else {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_CLASS_POINTER);
				this.dataOutputFragment.writeInt(IDIndex);
			}
		}
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeEnumData(final Enum<?> value,
			final EnumTypeImpl<?> enumType, final boolean declared) {
		final int enumOrdinal = value.ordinal();
		final boolean isSmallEnum = enumOrdinal <= SMALLENUM_MAX_ORDINAL;
		if (declared) {
			if (isSmallEnum) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_BYTE) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_FIXLEN_SMALLENUM_DECLARED);
				}
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_FIXLEN_LARGEENUM_DECLARED);
				}
			}
		} else {
			final GUID typeID = enumType.getID();
			final int IDIndex = this.objectIndexMap.tryGetIndex(typeID);
			if (IDIndex < 0) {
				if (isSmallEnum) {
					if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID + SIZE_BYTE) {
						return SERIALIZE_FAIL;
					} else {
						this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_SMALLENUM_GUID);
					}
				} else {
					if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID + SIZE_INT) {
						return SERIALIZE_FAIL;
					} else {
						this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_LARGEENUM_GUID);
					}
				}
				this.forceWriteGUIDValue(typeID);
			} else {
				if (isSmallEnum) {
					if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER + SIZE_BYTE) {
						return SERIALIZE_FAIL;
					} else {
						this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_SMALLENUM_POINTER);
					}
				} else {
					if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER + SIZE_INT) {
						return SERIALIZE_FAIL;
					} else {
						this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_LARGEENUM_POINTER);
					}
				}
				this.dataOutputFragment.writeInt(IDIndex);
			}
		}
		if (isSmallEnum) {
			this.dataOutputFragment.writeByte((byte) enumOrdinal);
		} else {
			this.dataOutputFragment.writeInt(enumOrdinal);
		}
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeObjectArrayData(final ObjectArrayDataType arrayType,
			final Object[] value) {
		final GUID typeID = arrayType.getID();
		final int IDIndex = this.objectIndexMap.tryGetIndex(typeID);
		final boolean isEmptyArray = value.length == 0;
		if (IDIndex < 0) {
			if (isEmptyArray) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_EMPTYOBJECTARRAY_GUID);
					this.forceWriteGUIDValue(typeID);
				}
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID + SIZE_INT) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_OBJECTARRAY_GUID);
					this.forceWriteGUIDValue(typeID);
					this.dataOutputFragment.writeInt(value.length);
				}
			}
		} else {
			if (isEmptyArray) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_EMPTYOBJECTARRAY_POINTER);
					this.dataOutputFragment.writeInt(IDIndex);
				}
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER + SIZE_INT) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_OBJECTARRAY_POINTER);
					this.dataOutputFragment.writeInt(IDIndex);
					this.dataOutputFragment.writeInt(value.length);
				}
			}
		}
		this.stack.pushArray(value, value.length);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeStructData(final Object value,
			final StructDefineImpl structDefine, final boolean declared) {
		if (declared) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_FIXLEN_STRUCT_DECLARED);
			}
		} else {
			final GUID typeID = structDefine.getID();
			final int IDIndex = this.objectIndexMap.tryGetIndex(typeID);
			if (IDIndex < 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_STRUCT_GUID);
					this.forceWriteGUIDValue(typeID);
				}
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_STRUCT_POINTER);
					this.dataOutputFragment.writeInt(IDIndex);
				}
			}
		}
		this.stack.pushStruct(value, structDefine);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeCustomSerializeDataObject(final Object value,
			final ObjectDataType type, DataObjectTranslator<?, ?> serializer) {
		final GUID typeID = type.getID();
		final int IDIndex = this.objectIndexMap.tryGetIndex(typeID);
		if (IDIndex < 0) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID + SIZE_SHORT) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_CUSTOM_GUID);
				this.forceWriteGUIDValue(typeID);
			}
		} else {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER + SIZE_SHORT) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_CUSTOM_POINTER);
				this.dataOutputFragment.writeInt(IDIndex);
			}
		}
		this.dataOutputFragment.writeShort(serializer.getVersion());
		this.stack.pushCustom(value, type);
		this.objectIndexMap.tryPutObject(value);
		return SERIALIZE_SUCCESS;
	}

	@Override
	final boolean writeCustomData(final Object value) {
		return this.writeObject(value, null);
	}

	@Override
	final boolean writeObjectArrayElement(final Object value) {
		return this.writeObject(value, null);
	}

	private boolean writePointer(final int pointer) {
		if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
			return SERIALIZE_FAIL;
		} else {
			this.dataOutputFragment.writeByte(HEAD_MARK_POINTER);
			this.dataOutputFragment.writeInt(pointer);
			return SERIALIZE_SUCCESS;
		}
	}

	private boolean writeNullObject() {
		switch (this.CState) {
		case NULL:
			if (this.CCount < CONTINUOUS_NULL_MAX_COUNT) {
				this.CCount++;
				this.CValue = (byte) this.CCount;
				return SERIALIZE_SUCCESS;
			}
		case BOOLEAN:
			if (!this.tryFinishWriteC()) {
				return SERIALIZE_FAIL;
			}
		}
		this.CState = ContinuousState.NULL;
		this.CCount = 1;
		this.CValue = 1;
		return SERIALIZE_SUCCESS;
	}

	private void forceWriteGUIDValue(final GUID value) {
		this.dataOutputFragment.writeLong(value.getMostSigBits());
		this.dataOutputFragment.writeLong(value.getLeastSigBits());
		this.objectIndexMap.tryPutObject(value);
	}

	private boolean writeVarLengthObject(final FixLengthObject empty,
			final VarLengthObject notEmpty, final Object value, final int length) {
		if (this.VLOWriter == null) {
			if (length == 0) {
				if (this.dataOutputFragment.remain() < SIZE_HEAD) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(empty.headCode);
					this.objectIndexMap.tryPutObject(value);
				}
			} else {
				if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_INT) {
					return SERIALIZE_FAIL;
				} else {
					this.dataOutputFragment.writeByte(notEmpty.headCode);
					this.dataOutputFragment.writeInt(length);
					final int wroteLength = notEmpty.writeElement(this.dataOutputFragment, value, 0, length);
					if (wroteLength < length) {
						this.VLOWriter = notEmpty;
						this.VLOObject = value;
						this.VLOLength = length;
						this.VLOWrote = wroteLength;
						return SERIALIZE_FAIL;
					} else {
						this.objectIndexMap.tryPutObject(value);
					}
				}
			}
		} else {
			if (this.VLOWriter != notEmpty) {
				throw serializeException();
			}
			final int wroteLength = this.VLOWriter.writeElement(this.dataOutputFragment, this.VLOObject, this.VLOWrote, this.VLOLength);
			if (wroteLength < this.VLOLength) {
				this.VLOWrote = wroteLength;
				return SERIALIZE_FAIL;
			} else {
				this.objectIndexMap.tryPutObject(this.VLOObject);
				this.VLOWriter = null;
				this.VLOObject = null;
				this.VLOLength = 0;
				this.VLOWrote = 0;
			}
		}
		return SERIALIZE_SUCCESS;
	}

	private final boolean writeDataType(final DataType type) {
		final GUID typeID = type.getID();
		final int IDIndex = this.objectIndexMap.tryGetIndex(typeID);
		if (IDIndex < 0) {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_GUID) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_TYPE_GUID);
				this.forceWriteGUIDValue(typeID);
			}
		} else {
			if (this.dataOutputFragment.remain() < SIZE_HEAD + SIZE_POINTER) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(HEAD_WITHTYPEID_TYPE_POINTER);
				this.dataOutputFragment.writeInt(IDIndex);
			}
		}
		this.objectIndexMap.tryPutObject(type);
		return SERIALIZE_SUCCESS;
	}

	private boolean tryFinishWriteC() {
		if (this.CState != ContinuousState.NONE) {
			if (this.dataOutputFragment.remain() < SIZE_BYTE) {
				return SERIALIZE_FAIL;
			} else {
				this.dataOutputFragment.writeByte(this.CValue);
				this.CState = ContinuousState.NONE;
				this.CValue = 0;
				this.CCount = 0;
			}
		}
		return SERIALIZE_SUCCESS;
	}

	private final boolean tryFinishSerialize() {
		if (this.tryFinishWriteC()) {
			this.reset();
			return SERIALIZE_SUCCESS;
		} else {
			return SERIALIZE_FAIL;
		}
	}

	private final boolean processStack() {
		for (;;) {
			Stack.StackEntry topStackEntry = this.stack.topStackEntry;
			if (this.stack.beginSerializeField(this)) {
				if (this.stack.endSerializeField(topStackEntry)) {
					return true;
				}
			} else {
				return false;
			}
		}
	}

	private static final boolean SERIALIZE_SUCCESS = true;

	private static final boolean SERIALIZE_FAIL = !SERIALIZE_SUCCESS;

	private final Stack stack;

	private final ObjectIndexMap objectIndexMap;

	private Object rootObject;

	private DataOutputFragment dataOutputFragment;

	private VarLengthObject VLOWriter;

	private Object VLOObject;

	private int VLOLength;

	private int VLOWrote;

	private ContinuousState CState;

	private byte CValue;

	private int CCount;

	private static final class Stack {

		private Stack() {
			this.stack = new StackEntry[STACK_INIT_DEEPNESS];
			this.deepness = -1;
		}

		final boolean isEmpty() {
			return this.deepness == -1;
		}

		final void reset() {
			this.deepness = -1;
			this.topStackEntry = null;
		}

		final void pushStruct(final Object object, final DataType dataType) {
			final StructFieldDefineImpl firstField = ((StructDefineImpl) dataType).getFirstNIOSerializableField();
			if (firstField != null) {
				final StackEntry topStackEntry = this.newTopStackEntry();
				topStackEntry.object = object;
				topStackEntry.firstField = firstField;
			}
		}

		final void pushArray(final Object array, final int length) {
			final int index = length - 1;
			if (index < 0) {
				return;
			}
			final StackEntry topStackEntry = this.newTopStackEntry();
			topStackEntry.object = array;
			topStackEntry.firstField = ObjectArrayFieldDefine.INSTANCE;
			topStackEntry.index = index;
		}

		@SuppressWarnings("unchecked")
		final void pushCustom(final Object custom, final DataType dataType) {
			final StackEntry topStackEntry = this.newTopStackEntry();
			DataObjectTranslator translator = ((ObjectDataTypeBase) dataType).getDataObjectTranslator();
			topStackEntry.object = translator.toDelegateObject(custom);
			topStackEntry.firstField = CustomFieldDefine.INSTANCE;
		}

		final boolean beginSerializeField(final NSerializer serializer) {
			final StackEntry topStackEntry = this.topStackEntry;
			final Object object;
			if (topStackEntry.index == -1) {
				object = topStackEntry.object;
			} else {
				object = Array.get(topStackEntry.object, topStackEntry.index);
			}
			return topStackEntry.firstField.serialize(serializer, object);
		}

		final boolean endSerializeField(StackEntry topStackEntry) {
			if (topStackEntry != this.topStackEntry) {
				if (topStackEntry.index >= 0) {
					if ((--topStackEntry.index) == -1) {
						topStackEntry.firstField = null;
					}
				} else {
					final StructFieldDefineImpl firstField = topStackEntry.firstField;
					if (firstField.getClass() == CustomFieldDefine.class) {
						topStackEntry.firstField = null;
					} else {
						topStackEntry.firstField = firstField.nextNIOSerializableField;
					}
				}
				return false;
			} else {
				if (topStackEntry.index >= 0) {
					if ((--topStackEntry.index) >= 0) {
						return false;
					}
					topStackEntry.firstField = null;
				} else {
					final StructFieldDefineImpl firstField = topStackEntry.firstField;
					if (firstField.getClass() == CustomFieldDefine.class) {
						topStackEntry.firstField = null;
					} else {
						topStackEntry.firstField = firstField.nextNIOSerializableField;
						if (topStackEntry.firstField != null) {
							return false;
						}
					}
				}
				for (; true;) {
					topStackEntry.object = null;
					if ((--this.deepness) >= 0) {
						topStackEntry = this.topStackEntry = this.stack[this.deepness];
						if (topStackEntry.firstField != null) {
							return false;
						}
					} else {
						return true;
					}
				}
			}
		}

		private final StackEntry newTopStackEntry() {
			if (++this.deepness == this.stack.length) {
				StackEntry[] newStack = new StackEntry[this.stack.length + STACK_INIT_DEEPNESS];
				System.arraycopy(this.stack, 0, newStack, 0, this.stack.length);
				this.stack = newStack;
			}
			StackEntry stackEntry = this.stack[this.deepness];
			if (stackEntry == null) {
				stackEntry = this.stack[this.deepness] = new StackEntry();
			}
			return this.topStackEntry = stackEntry;
		}

		private static final int STACK_INIT_DEEPNESS = 4;

		private int deepness;

		private StackEntry[] stack;

		private StackEntry topStackEntry;

		private static final class StackEntry {

			private StackEntry() {
				this.index = -1;
			}

			Object object;

			StructFieldDefineImpl firstField;

			int index;

		}

	}

	private static final class ObjectIndexMap {

		ObjectIndexMap() {
			this.map = new MapEntry[16];
		}

		final void tryPutObject(final Object object) {
			final int hashCode = System.identityHashCode(object);
			final int oldLength = this.map.length;
			int hashIndex = hashCode & (oldLength - 1);
			if (++this.size > oldLength * 0.75) {
				final int newLength = oldLength * 2;
				final MapEntry[] newMap = new MapEntry[newLength];
				for (int index = 0; index < oldLength; index++) {
					for (MapEntry entry = this.map[index], next; entry != null; entry = next) {
						hashIndex = System.identityHashCode(entry.object) & (newLength - 1);
						next = entry.next;
						entry.next = newMap[hashIndex];
						newMap[hashIndex] = entry;
					}
				}
				this.map = newMap;
				hashIndex = hashCode & (newLength - 1);
			}
			final int objIndex = this.objectIndex++;
			this.map[hashIndex] = new MapEntry(object, objIndex, this.map[hashIndex]);
		}

		final int tryGetIndex(final Object object) {
			final int hashCode = System.identityHashCode(object);
			final int oldLength = this.map.length;
			int hashIndex = hashCode & (oldLength - 1);
			for (MapEntry entry = this.map[hashIndex]; entry != null; entry = entry.next) {
				if (entry.object == object) {
					return entry.index;
				}
			}
			return -1;
		}

		final void reset() {
			Arrays.fill(this.map, null);
			this.size = 0;
			this.objectIndex = 0;
		}

		@Override
		public String toString() {
			final Object[] objects = new Object[this.size];
			for (MapEntry entry : this.map) {
				while (entry != null) {
					objects[entry.index] = entry.object;
					entry = entry.next;
				}
			}
			int index = 0;
			StringBuilder sb = new StringBuilder();
			for (Object object : objects) {
				sb.append(index++).append('\t').append(object).append('\n');
			}
			return sb.toString();
		}

		private MapEntry[] map;

		private int size;

		private int objectIndex;

		private static class MapEntry {

			public final Object object;

			public final int index;

			public MapEntry next;

			MapEntry(final Object object, final int index, final MapEntry next) {
				this.object = object;
				this.index = index;
				this.next = next;
			}

		}

	}

}
