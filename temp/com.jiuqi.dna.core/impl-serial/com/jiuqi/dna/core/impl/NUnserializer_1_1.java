package com.jiuqi.dna.core.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.DataObjectTranslator;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;

public final class NUnserializer_1_1 extends NUnserializer implements
		NSerializeBase_1_1 {

	public static final NUnserializerFactory FACTORY = new NUnserializerFactory(SERIALIZE_VERSION) {

		@Override
		public final NUnserializer newNUnserializer(
				ObjectTypeQuerier objectTypeQuerier) {
			return new NUnserializer_1_1(objectTypeQuerier);
		}

	};

	public NUnserializer_1_1(ObjectTypeQuerier objectTypeQuerier) {
		super(objectTypeQuerier);
		this.stack = new Stack();
		this.objectIndex = new ArrayList<Object>();
		this.rootObject = NONE_OBJECT;
		this.hint = NONE_OBJECT;
		this.CState = ContinuousState.NONE;
	}

	@Override
	public final short getVersion() {
		return SERIALIZE_VERSION;
	}

	@Override
	public final boolean unserializeStart(final DataInputFragment fragment,
			final Object destHint) {
		if (!this.isUnserialized()) {
			throw new IllegalStateException("当前反序列化还未完成");
		}
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		this.dataInputFragment = fragment;
		this.rootObject = NONE_OBJECT;
		this.hint = destHint;
		final Object object = this.readObject(null, destHint);
		boolean result = this.unserializeSuccess;
		if (result) {
			if (this.stack.isEmpty()) {
				this.rootObject = object;
			} else {
				result = this.processStack();
			}
		}
		if (result) {
			this.reset();
			return true;
		}
		return false;
	}

	@Override
	public final boolean unserializeRest(final DataInputFragment fragment) {
		if (this.isUnserialized()) {
			throw new IllegalStateException("当前反序列化已经完成");
		}
		if (fragment == null) {
			throw new NullArgumentException("fragment");
		}
		this.dataInputFragment = fragment;
		boolean result;
		if (this.stack.isEmpty()) {
			// 栈为空表示反序列化还没有开始或者root对象上没有字段（简单类型）
			if (this.VLOReader == null) {
				// VLOReader表示正在反序列化的对象的读取器，null说明反序列化还没有开始
				final Object object = this.readObject(null, this.hint);
				result = this.unserializeSuccess;
				if (result) {
					if (this.stack.isEmpty()) {
						this.rootObject = object;
					} else {
						result = this.processStack();
					}
				}
			} else {
				// 表示root对象是简单类型，并且读取没有完成，应当继续读
				final Object object = this.readVarLengthObject(this.VLOReader);
				result = this.unserializeSuccess;
				if (result) {
					this.rootObject = object;
				}
			}
		} else {
			result = this.processStack();
		}
		if (result) {
			this.reset();
			return true;
		}
		return false;
	}

	@Override
	public final boolean isUnserialized() {
		return this.hint == NONE_OBJECT;
	}

	@Override
	public final Object getUnserialzedObject() {
		if (this.rootObject == NONE_OBJECT) {
			throw new IllegalStateException("当前反序列化尚未完成");
		}
		return this.rootObject;
	}

	@Override
	public final void reset() {
		this.hint = NONE_OBJECT;
		this.VLOReader = null;
		this.CState = ContinuousState.NONE;
		this.stack.reset();
		this.objectIndex.clear();
		this.unserializeSuccess = true;
	}

	@Override
	final boolean readBoolean() {
		final boolean result;
		if (this.CState == ContinuousState.BOOLEAN) {
			result = (byte) (this.CValue & 0x01) == 1 ? true : false;
			this.CValue >>>= 1;
			if ((this.CRest -= 1) == 0) {
				this.CState = ContinuousState.NONE;
				this.CValue = 0;
				this.CRest = 0;
			}
		} else {
			if (this.CState != ContinuousState.NONE) {
				throw unserializeException();
			}
			if (this.dataInputFragment.remain() < SIZE_BYTE) {
				this.unserializeSuccess = false;
				return false;
			}
			final byte continuousBooleanInfo = this.dataInputFragment.readByte();
			result = (continuousBooleanInfo & 0x01) == 1;
			this.CState = ContinuousState.BOOLEAN;
			this.CValue = (byte) (continuousBooleanInfo >>> 1);
			this.CRest = CONTINUOUS_BOOLEAN_MAX_COUNT - 1;
			this.unserializeSuccess = true;
		}
		return result;
	}

	@Override
	final byte readByte() {
		if (this.dataInputFragment.remain() < SIZE_BYTE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readByte();
		}
	}

	@Override
	final char readChar() {
		if (this.dataInputFragment.remain() < SIZE_CHAR) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readChar();
		}
	}

	@Override
	final short readShort() {
		if (this.dataInputFragment.remain() < SIZE_SHORT) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readShort();
		}
	}

	@Override
	final int readInt() {
		if (this.dataInputFragment.remain() < SIZE_INT) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readInt();
		}
	}

	@Override
	final float readFloat() {
		if (this.dataInputFragment.remain() < SIZE_FLOAT) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readFloat();
		}
	}

	@Override
	final long readLong() {
		if (this.dataInputFragment.remain() < SIZE_LONG) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readLong();
		}
	}

	@Override
	final double readDouble() {
		if (this.dataInputFragment.remain() < SIZE_DOUBLE) {
			this.unserializeSuccess = false;
			return 0;
		} else {
			this.unserializeSuccess = true;
			return this.dataInputFragment.readDouble();
		}
	}

	@Override
	final Object readByteArrayField() {
		if (this.VLOReader == null) {
			if (this.CState == ContinuousState.NULL) {
				this.readNullObjectFromC();
				return null;
			}
			if (this.dataInputFragment.remain() < SIZE_HEAD) {
				this.unserializeSuccess = false;
				return null;
			} else {
				final byte head = this.dataInputFragment.readByte();
				final ObjectReader objectReader = OBJECT_READERS[(head >>> 5 & 7)][head & SORTCODE_DEMASK];
				return objectReader.read(this, null, null);
			}
		}
		return this.readVarLengthObject(VarLengthObject.BYTEARRAY);
	}

	@Override
	final Object readStringField() {
		if (this.VLOReader == null) {
			if (this.CState == ContinuousState.NULL) {
				this.readNullObjectFromC();
				return null;
			}
			if (this.dataInputFragment.remain() < SIZE_HEAD) {
				this.unserializeSuccess = false;
				return null;
			} else {
				final byte head = this.dataInputFragment.readByte();
				final ObjectReader objectReader = OBJECT_READERS[(head >>> 5 & 7)][head & SORTCODE_DEMASK];
				return objectReader.read(this, null, null);
			}
		}
		return this.readVarLengthObject(VarLengthObject.STRING);
	}

	@Override
	final long readDateField() {
		return this.readLong();
	}

	@Override
	final Object readEnumField(final DataType declaredType) {
		if (this.CState == ContinuousState.NULL) {
			this.readNullObjectFromC();
			return null;
		}
		if (this.dataInputFragment.remain() < SIZE_HEAD) {
			this.unserializeSuccess = false;
			return null;
		} else {
			final byte head = this.dataInputFragment.readByte();
			final ObjectReader objectReader = OBJECT_READERS[(head >>> 5 & 7)][head & SORTCODE_DEMASK];
			return objectReader.read(this, declaredType, null);
		}
	}

	@Override
	final Object readGUIDField() {
		if (this.CState == ContinuousState.NULL) {
			this.readNullObjectFromC();
			return null;
		}
		if (this.dataInputFragment.remain() < SIZE_HEAD) {
			this.unserializeSuccess = false;
			return null;
		} else {
			final byte head = this.dataInputFragment.readByte();
			final ObjectReader objectReader = OBJECT_READERS[(head >>> 5 & 7)][head & SORTCODE_DEMASK];
			final Object id = objectReader.read(this, null, null);
			if (id != null) {
				this.unserializeSuccess = true;
			}
			return id;
		}
	}

	@Override
	final Object readObject(final DataType declaredType, final Object hint) {
		if (this.VLOReader == null) {
			if (this.CState == ContinuousState.NULL) {
				this.readNullObjectFromC();
				return null;
			} else if (this.CState == ContinuousState.BOOLEAN) {
				this.CState = ContinuousState.NONE;
			}
			if (this.dataInputFragment.remain() < SIZE_HEAD) {
				this.unserializeSuccess = false;
				return null;
			} else {
				final byte head = this.dataInputFragment.readByte();
				final ObjectReader objectReader = OBJECT_READERS[(head >>> 5 & 7)][head & SORTCODE_DEMASK];
				final Object result = objectReader.read(this, declaredType, hint);
				if (!this.unserializeSuccess && objectReader instanceof WithTypeIDObject) {
					if (((WithTypeIDObject) objectReader).byPointer) {
						this.dataInputFragment.skip(-(SIZE_HEAD + SIZE_POINTER));
					} else {
						this.dataInputFragment.skip(-(SIZE_HEAD + SIZE_GUID));
						this.objectIndex.remove(this.objectIndex.size() - 1);
					}
				}
				return result;
			}
		} else {
			return this.readVarLengthObject(this.VLOReader);
		}
	}

	@Override
	final void readObjectArrayElement(final Object hint,
			DataTranslatorHelper<?, ?> dth) {
		final Stack.StackEntry topStackEntry = this.stack.topStackEntry;
		this.stack.readObjectArrayElement(topStackEntry, this.readObject(null, hint), dth);
	}

	@Override
	final void readCustomObject(final Object hint) {
		final Stack.StackEntry topStackEntry = this.stack.topStackEntry;
		topStackEntry.dest = this.readObject(null, hint);
	}

	final Object readBooleanObject(final boolean value) {
		final Boolean result = Boolean.valueOf(value);
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readByteObject() {
		final Byte result = Byte.valueOf(this.dataInputFragment.readByte());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readCharObject() {
		final Character result = Character.valueOf(this.dataInputFragment.readChar());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readShortObject() {
		final Short result = Short.valueOf(this.dataInputFragment.readShort());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readIntObject() {
		final Integer result = Integer.valueOf(this.dataInputFragment.readInt());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readFloatObject() {
		final Float result = new Float(this.dataInputFragment.readFloat());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readLongObject() {
		final Long result = Long.valueOf(this.dataInputFragment.readLong());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readDoubleObject() {
		final Double result = new Double(this.dataInputFragment.readDouble());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyGUID() {
		final Object result = com.jiuqi.dna.core.type.GUID.valueOf(0L, 0L);
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyString() {
		final Object result = "";
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyBooleanArray() {
		final Object result = new boolean[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyByteArray() {
		final Object result = new byte[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyCharArray() {
		final Object result = new char[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyShortArray() {
		final Object result = new short[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyIntArray() {
		final Object result = new int[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyFloatArray() {
		final Object result = new float[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyLongArray() {
		final Object result = new long[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyDoubleArray() {
		final Object result = new double[0];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readDataTypeByGUID() {
		final DataType dataType = this.tryGetDataType(this.readGUIDValue());
		if (this.unserializeSuccess = dataType != null) {
			this.objectIndex.add(dataType);
		}
		return dataType;
	}

	final Object readDataTypeByPointer() {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (this.unserializeSuccess = dataType != null) {
			this.objectIndex.add(dataType);
		}
		return dataType;
	}

	final Object readDataTypeByClassName() {
		final DataType dataType = this.readDataTypeOfData(false);
		this.objectIndex.add(dataType);
		this.unserializeSuccess = true;
		return dataType;
	}

	final Object readArrayDataTypeByClassName() {
		final DataType dataType = this.readDataTypeOfData(true);
		this.objectIndex.add(dataType);
		this.unserializeSuccess = true;
		return dataType;
	}

	final Object readSmallEnum(final DataType declaredType) {
		final Enum<?> result = ((EnumTypeImpl<?>) declaredType).enums[this.dataInputFragment.readByte()];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readSmallEnumByPointer() {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			final Enum<?> result = ((EnumTypeImpl<?>) dataType).enums[this.dataInputFragment.readByte()];
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
	}

	final Object readSmallEnumByGUID() {
		final GUID typeID = this.readGUIDValue();
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			final Enum<?> result = ((EnumTypeImpl<?>) dataType).enums[this.dataInputFragment.readByte()];
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
	}

	final Object readSmallEnumByClassName() {
		final Class<?> clazz = this.readDataTypeOfData(false).getJavaClass();
		final Object result = clazz.getEnumConstants()[this.dataInputFragment.readByte()];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readLargeEnum(final DataType declaredType) {
		final Enum<?> result = ((EnumTypeImpl<?>) declaredType).enums[this.dataInputFragment.readInt()];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readLargeEnumByPointer() {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			final Enum<?> result = ((EnumTypeImpl<?>) dataType).enums[this.dataInputFragment.readInt()];
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
	}

	final Object readLargeEnumByGUID() {
		final GUID typeID = this.readGUIDValue();
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			final Enum<?> result = ((EnumTypeImpl<?>) dataType).enums[this.dataInputFragment.readInt()];
			this.objectIndex.add(result);
			this.unserializeSuccess = true;
			return result;
		}
	}

	final Object readLargeEnumByClassName() {
		final Class<?> clazz = this.readDataTypeOfData(false).getJavaClass();
		final Object result = clazz.getEnumConstants()[this.dataInputFragment.readInt()];
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readStruct(final DataType declaredType, final Object hint) {
		this.unserializeSuccess = true;
		final Object result = this.stack.pushStruct(declaredType, hint);
		this.objectIndex.add(result);
		return result;
	}

	final Object readStructByPointer(final Object hint) {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Object result = this.stack.pushStruct(dataType, hint);
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readStructByGUID(final Object hint) {
		final GUID typeID = this.readGUIDValue();
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Object result = this.stack.pushStruct(dataType, hint);
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readStructByClassName(final Object hint) {
		final DataType dataType = this.readDataTypeOfData(false);
		final Object result = this.stack.pushStruct(dataType, hint);
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readClassByPointer() {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Class<?> result = dataType.getJavaClass();
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readClassByGUID() {
		final GUID typeID = this.readGUIDValue();
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Class<?> result = dataType.getJavaClass();
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readClassByClassName() {
		final Class<?> clazz = this.readDataTypeOfData(false).getJavaClass();
		this.objectIndex.add(clazz);
		this.unserializeSuccess = true;
		return clazz;
	}

	final Object readArrayClassByClassName() {
		final Class<?> clazz = this.readDataTypeOfData(true).getJavaClass();
		this.objectIndex.add(clazz);
		this.unserializeSuccess = true;
		return clazz;
	}

	final Object readCustomByPointer(final Object hint) {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			return this.stack.pushCustom(dataType, hint, this.dataInputFragment.readShort(), this.objectIndex);
		}
	}

	final Object readCustomByGUID(final Object hint) {
		final GUID typeID = this.readGUIDValue();
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			return this.stack.pushCustom(dataType, hint, this.dataInputFragment.readShort(), this.objectIndex);
		}
	}

	final Object readCustomByClassName(final Object hint) {
		final DataType dataType = this.readDataTypeOfData(false);
		final Object result = this.stack.pushCustom(dataType, hint, this.dataInputFragment.readShort(), this.objectIndex);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readObjectArrayByPointer(final Object hint) {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Object result = this.stack.pushArray(dataType, hint, this.dataInputFragment.readInt());
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readObjectArrayByGUID(final Object hint) {
		final GUID typeID = this.readGUIDValue();
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Object result = this.stack.pushArray(dataType, hint, this.dataInputFragment.readInt());
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readObjectArrayByClassName(final Object hint) {
		final DataType dataType = this.readDataTypeOfData(true);
		final Object result = this.stack.pushArray(dataType, hint, this.dataInputFragment.readInt());
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readEmptyObjectArrayByPointer(final Object hint) {
		final Object typeID = this.readPointer();
		final DataType dataType = this.tryGetDataType((GUID) typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Object result = hint == null ? ((ArrayDataTypeBase) dataType).newArray(0) : hint;
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readEmptyObjectArrayByGUID(final Object hint) {
		final GUID typeID = this.readGUIDValue();
		final DataType dataType = this.tryGetDataType(typeID);
		if (dataType == null) {
			this.unserializeSuccess = false;
			return null;
		} else {
			this.unserializeSuccess = true;
			final Object result = hint == null ? ((ArrayDataTypeBase) dataType).newArray(0) : hint;
			this.objectIndex.add(result);
			return result;
		}
	}

	final Object readEmptyObjectArrayByClassName(final Object hint) {
		final DataType dataType = this.readDataTypeOfData(true);
		final Object result = hint == null ? ((ArrayDataTypeBase) dataType).newArray(0) : hint;
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readPointer() {
		this.unserializeSuccess = true;
		final int index = this.dataInputFragment.readInt();
		return this.objectIndex.get(index);
	}

	final GUID readGUIDValue() {
		final GUID id = GUID.valueOf(this.dataInputFragment.readLong(), this.dataInputFragment.readLong());
		this.objectIndex.add(id);
		this.unserializeSuccess = true;
		return id;
	}

	final Object readVarLengthObject(final VarLengthObject VLOReader) {
		final Object object;
		if (this.VLOReader == null) {
			final int length = this.dataInputFragment.readInt();
			object = VLOReader.newVLDObject(length);
			final int read = VLOReader.readElement(this, this.dataInputFragment, object, 0, length);
			if (read < length) {
				this.VLOReader = VLOReader;
				this.VLOObject = object;
				this.VLOLength = length;
				this.VLORead = read;
				this.unserializeSuccess = false;
				return null;
			}
		} else {
			if (this.VLOReader != VLOReader) {
				throw unserializeException();
			}
			final int read = VLOReader.readElement(this, this.dataInputFragment, this.VLOObject, this.VLORead, this.VLOLength);
			if (read < this.VLOLength) {
				this.VLORead = read;
				this.unserializeSuccess = false;
				return null;
			} else {
				object = this.VLOObject;
				this.VLOReader = null;
			}
		}
		final Object result;
		if (VLOReader == VarLengthObject.STRING) {
			result = new String((char[]) object);
		} else {
			result = object;
		}
		this.objectIndex.add(result);
		this.unserializeSuccess = true;
		return result;
	}

	final Object readNull(byte head) {
		if (--head > 0) {
			this.CState = ContinuousState.NULL;
			this.CRest = head;
		}
		this.unserializeSuccess = true;
		return null;
	}

	final Object readUnserializable(final Object hint) {
		this.unserializeSuccess = true;
		return hint;
	}

	private final DataType readDataTypeOfData(boolean isArrayType) {
		final GUID typeID = this.readGUIDValue();
		DataType dataType = DataTypeBase.findDataType(typeID);
		final int classNameLen = this.dataInputFragment.readInt();
		if (dataType == null) {
			final DataInputFragment dataInputFragment = this.dataInputFragment;
			final char[] chars = new char[classNameLen];
			for (int index = 0; index < classNameLen; index++) {
				chars[index] = dataInputFragment.readChar();
			}
			try {
				Class<?> clazz = ApplicationImpl.getDefaultApp().loadClass(new String(chars));
				dataType = DataTypeBase.dataTypeOfJavaClass(clazz);
				if (isArrayType) {
					clazz = ArrayDataTypeBase.getArrayClassOf(clazz, DataTypeBase.calcArrayTypeDimension(typeID));
					dataType = DataTypeBase.dataTypeOfJavaClass(clazz);
				}
				if (!dataType.getID().equals(typeID)) {
					throw new RuntimeException(clazz + "类的结构与给定的结构不一致，给定的结构ID为" + typeID);
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			this.dataInputFragment.skip(classNameLen * 2);
		}
		return dataType;
	}

	private final void readNullObjectFromC() {
		if (--this.CRest == 0) {
			this.CState = ContinuousState.NONE;
		}
		this.unserializeSuccess = true;
	}

	private final boolean processStack() {
		Stack stack = this.stack;
		int deepness = stack.deepness;
		for (;;) {
			Stack.StackEntry topStackEntry = stack.beginUnserializeField(this);
			if (this.unserializeSuccess) {
				final Object result = stack.endUnserializeField(topStackEntry);
				if (stack.deepness <= deepness) {
					if (result != null) {
						this.rootObject = result;
						return true;
					}
				}
				deepness = stack.deepness;
			} else {
				return false;
			}
		}
	}

	private Object rootObject;

	private Object hint;

	private final Stack stack;

	private DataInputFragment dataInputFragment;

	private boolean unserializeSuccess;

	private final ArrayList<Object> objectIndex;

	private VarLengthObject VLOReader;

	private Object VLOObject;

	private int VLOLength;

	private int VLORead;

	private ContinuousState CState;

	private byte CValue;

	private int CRest;

	private static final ObjectReader[][] OBJECT_READERS = ObjectReaderContainer.OBJECT_READERS;

	private static final class Stack {

		Stack() {
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

		final Object pushStruct(final DataType dataType, final Object hint) {
			final StructDefineImpl structDefine = (StructDefineImpl) dataType;
			final Object dest;
			if (hint == null) {
				dest = structDefine.newEmptySO();
			} else {
				dest = hint;
			}
			final StructFieldDefineImpl firstField = structDefine.getFirstNIOSerializableField();
			if (firstField != null) {
				StackEntry stackEntry = this.newTopStackEntry();
				stackEntry.hint = hint;
				stackEntry.dest = dest;
				stackEntry.firstField = firstField;
			}
			return dest;
		}

		final Object pushArray(final DataType dataType, Object hint,
				final int length) {
			final int index = length - 1;
			final ArrayDataTypeBase arrayDataType = (ArrayDataTypeBase) dataType;
			if (index < 0) {
				return arrayDataType.newArray(0);
			}
			final StackEntry stackEntry = this.newTopStackEntry();
			final Object dest;
			if (hint != null && Array.getLength(hint) == length) {
				dest = hint;
			} else {
				dest = arrayDataType.newArray(length);
			}
			stackEntry.hint = hint;
			stackEntry.dest = dest;
			stackEntry.firstField = ObjectArrayFieldDefine.INSTANCE;
			stackEntry.index = index;
			return dest;
		}

		@SuppressWarnings("unchecked")
		final Object pushCustom(final DataType dataType, final Object hint,
				final short version, final ArrayList<Object> objectIndex) {
			final StackEntry stackEntry = this.newTopStackEntry();
			final ObjectDataTypeBase objectType = (ObjectDataTypeBase) dataType;
			final DataObjectTranslator<?, ?> translator = objectType.getDataObjectTranslator();
			final SerializeDataTranslatorHelper translatorHelper = new SerializeDataTranslatorHelper(translator, objectIndex, version, hint != null);
			stackEntry.hint = hint;
			stackEntry.firstField = CustomFieldDefine.INSTANCE;
			stackEntry.translatorHelper = translatorHelper;
			return translatorHelper;
		}

		final StackEntry beginUnserializeField(final NUnserializer unserializer) {
			final StackEntry topStackEntry = this.topStackEntry;
			final Object entryHint = topStackEntry.hint;
			Object hint;
			if (entryHint == null) {
				hint = null;
			} else {
				final int index = topStackEntry.index;
				if (index != -1) {
					if (index < Array.getLength(entryHint)) {
						hint = Array.get(entryHint, index);
					} else {
						hint = null;
					}
				} else if (topStackEntry.translatorHelper == null) {
					hint = topStackEntry.firstField.internalGetFieldValueAsObject(entryHint);
				} else {
					hint = null;
				}
			}
			topStackEntry.firstField.unserialize(unserializer, topStackEntry.dest, hint, topStackEntry.DTH);
			return topStackEntry;
		}

		@SuppressWarnings("unchecked")
		final Object endUnserializeField(StackEntry topStackEntry) {
			if (topStackEntry != this.topStackEntry) {
				if (topStackEntry.index >= 0) {
					if (--topStackEntry.index == -1) {
						topStackEntry.firstField = null;
					}
				} else if (topStackEntry.translatorHelper == null) {
					topStackEntry.firstField = topStackEntry.firstField.nextNIOSerializableField;
				} else {
					topStackEntry.firstField = null;
				}
				return null;
			} else {
				Object object = topStackEntry.dest;
				if (topStackEntry.index >= 0) {
					if (--topStackEntry.index >= 0) {
						return null;
					}
					object = topStackEntry.dest;
				} else if (topStackEntry.translatorHelper == null) {
					if (topStackEntry.firstField == null || topStackEntry.firstField.nextNIOSerializableField == null) {
						object = topStackEntry.dest;
					} else {
						topStackEntry.firstField = topStackEntry.firstField.nextNIOSerializableField;
						return null;
					}
				} else {
					object = topStackEntry.translatorHelper.translate(object, topStackEntry.hint);
					topStackEntry.translatorHelper = null;
				}
				for (; true;) {
					topStackEntry.firstField = null;
					topStackEntry.hint = null;
					topStackEntry.dest = null;
					if (--this.deepness >= 0) {
						topStackEntry = this.topStackEntry = this.stack[this.deepness];
						if (topStackEntry.firstField != null) {
							return null;
						} else if (topStackEntry.translatorHelper != null) {
							object = topStackEntry.translatorHelper.translate(object, topStackEntry.hint);
							topStackEntry.translatorHelper = null;
						} else {
							object = topStackEntry.dest;
						}
					} else {
						this.topStackEntry = null;
						return object;
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		final void readObjectArrayElement(final StackEntry topStackEntry,
				final Object element, DataTranslatorHelper<?, ?> dth) {
			if (element != null && element.getClass() == SerializeDataTranslatorHelper.class) {
				final SerializeDataTranslatorHelper helper = (SerializeDataTranslatorHelper) element;
				helper.appendArrayItemFeedbacker((Object[]) (topStackEntry.dest), dth, topStackEntry.index);
			} else {
				Array.set(topStackEntry.dest, topStackEntry.index, element);
			}
		}

		@SuppressWarnings("unchecked")
		private final StackEntry newTopStackEntry() {
			final SerializeDataTranslatorHelper DTH = (this.deepness == -1) ? null : this.topStackEntry.translatorHelper;
			if (++this.deepness == this.stack.length) {
				StackEntry[] newStack = new StackEntry[this.stack.length + STACK_INIT_DEEPNESS];
				System.arraycopy(this.stack, 0, newStack, 0, this.stack.length);
				this.stack = newStack;
			}
			StackEntry stackEntry = this.stack[this.deepness];
			if (stackEntry == null) {
				stackEntry = this.stack[this.deepness] = new StackEntry();
			}
			stackEntry.DTH = DTH;
			return this.topStackEntry = stackEntry;
		}

		private static final int STACK_INIT_DEEPNESS = 4;

		private StackEntry[] stack;

		private int deepness;

		private StackEntry topStackEntry;

		static final class StackEntry {

			protected StackEntry() {
				this.index = -1;
			}

			Object hint;

			Object dest;

			@SuppressWarnings("unchecked")
			SerializeDataTranslatorHelper translatorHelper;

			StructFieldDefineImpl firstField;

			int index;

			@SuppressWarnings("unchecked")
			SerializeDataTranslatorHelper DTH;

		}

	}

}
