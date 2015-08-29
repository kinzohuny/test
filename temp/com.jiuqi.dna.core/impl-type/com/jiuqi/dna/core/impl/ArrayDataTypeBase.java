package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.lang.reflect.Array;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.OBJAContext.CloneDataTranslatorHelper;
import com.jiuqi.dna.core.type.ArrayDataType;
import com.jiuqi.dna.core.type.GUID;

/**
 * 数组复制器
 * 
 * @author gaojingxin
 * 
 */
abstract class ArrayDataTypeBase extends ObjectDataTypeBase implements
		ArrayDataType {

	final Class<?> componentJavaClass;
	final boolean isPrimitive;
	final DataTypeInternal componentType;
	private String toString;

	@Override
	public String toString() {
		if (this.toString == null) {
			this.toString = this.getComponentType().toString() + "[]";
		}
		return this.toString;
	};

	@Override
	protected GUID calcTypeID() {
		return calcArryTypeID(this.componentType);
	}

	public final DataTypeInternal getComponentType() {
		return this.componentType;
	}

	public final Class<?> getComponentJavaClass() {
		return this.componentJavaClass;
	}

	public final boolean isPrimitive() {
		return this.isPrimitive;
	}

	@Override
	public final boolean isArray() {
		return true;
	}

	@Override
	final void regThisDataTypeInConstructor() {
	}

	void regArrayDataTypeInConstructor() {
		regDataType(this);
	}

	ArrayDataTypeBase(Class<?> arrayClass, DataTypeInternal componentType) {
		super(arrayClass);
		this.parseRootComponentClassAndDemension(arrayClass);
		this.componentJavaClass = arrayClass.getComponentType();
		this.isPrimitive = this.componentJavaClass.isPrimitive();
		if (componentType == null) {
			componentType = DataTypeBase.dataTypeOfJavaClass(this.componentJavaClass);
		} else {
			final Class<?> cc = componentType.getJavaClass();
			if (cc != null && cc != this.componentJavaClass) {
				throw new IllegalArgumentException("无效的元素类型:" + componentType);
			}
		}
		this.componentType = componentType;
		if (this.getRegClass() != null) {
			this.componentType.setArrayOf(this);
		}
		this.regArrayDataTypeInConstructor();
	}

	public final Object newArray(int length) {
		return Array.newInstance(this.componentJavaClass, length);
	}

	@Override
	public final Object assignNoCheckSrc(Object src, Object destHint,
			OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		if (this.isPrimitive) {
			final int sl = Array.getLength(src);
			Object dest;
			if (sl == 0) {
				dest = src;
			} else {
				if (destHint == null) {
					dest = Array.newInstance(this.componentJavaClass, sl);
				} else {
					final int dl = Array.getLength(destHint);
					if (dl != sl) {
						dest = Array.newInstance(this.componentJavaClass, sl);
					} else {
						dest = destHint;
					}
				}
				System.arraycopy(src, 0, dest, 0, sl);
			}
			if (dth == null) {
				objaContext.putRef(src, dest);
			}
			return dest;
		} else {
			final Object[] srcA = (Object[]) src;
			Object[] destHintA = (Object[]) destHint;
			final Object[] destA;
			final int sl = srcA.length;
			final int dl;
			if (sl == 0) {
				destA = srcA;
				dl = 0;
			} else {
				dl = destHintA != null ? destHintA.length : 0;
				destA = dl == sl ? destHintA : (Object[]) Array.newInstance(this.componentJavaClass, sl);
			}
			if (dth == null) {
				objaContext.putRef(src, destA);
			}
			for (int i = 0; i < sl; i++) {
				final Object srcItemObj = srcA[i];
				if (srcItemObj == null) {
					destA[i] = null;
					continue;
				}
				final Object destItemHint;
				if (i < dl) {
					destItemHint = destHintA[i];
					if (srcItemObj == destItemHint) {
						continue;
					}
				} else {
					destItemHint = null;
				}
				final Object destItem = objaContext.find(srcItemObj);
				if (destItem == null) {
					destA[i] = objaContext.doAssign(srcItemObj, destItemHint, null);
				} else if (destItem.getClass() == CloneDataTranslatorHelper.class) {
					((CloneDataTranslatorHelper<?, ?>) destItem).appendArrayItemFeedbacker(destA, dth, i);
				} else {
					destA[i] = destItem;
				}
			}
			return destA;
		}
	}

	@Override
	public final boolean isInstance(Object obj) {
		if (obj == null) {
			return false;
		}
		Class<?> cl = obj.getClass();
		if (!cl.isArray()) {
			return false;
		}
		cl = cl.getComponentType();
		if (cl.isPrimitive() && cl == this.componentJavaClass) {
			return true;
		}
		return this.componentJavaClass.isAssignableFrom(cl); // 接受兼容的类型。
	}

	// //////////////////////////////////
	// Serialization

	@Override
	public final boolean supportSerialization() {
		return true;
	}

	@Override
	public abstract void writeObjectData(InternalSerializer serializer,
			Object obj) throws IOException, StructDefineNotFoundException;

	@Override
	public abstract Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException;

	// final short getDimension() {
	// return this.dimension;
	// }

	final Class<?> getRootComponentClass() {
		return this.rootComponentClass;
	}

	// private short dimension;

	private Class<?> rootComponentClass;

	private final void parseRootComponentClassAndDemension(Class<?> clazz) {
		short dimension = 0;
		while (clazz.isArray()) {
			dimension++;
			clazz = clazz.getComponentType();
		}
		// this.dimension = dimension;
		this.rootComponentClass = clazz;
	}

	static final short dimensionOfClass(Class<?> clazz) {
		if (clazz == null) {
			throw new NullArgumentException("clazz");
		}
		short dimension = 0;
		while (clazz.isArray()) {
			dimension++;
			clazz = clazz.getComponentType();
		}
		return dimension;
	}

	static final Class<?> getArrayClassOf(final Class<?> componentClass,
			final int dimension) {
		if (componentClass == null) {
			throw new NullArgumentException("componentClass");
		}
		if (dimension < 1 || 255 < dimension) {
			throw new IllegalArgumentException("参数[dimension]的取值范围应为[1, 255]。" + dimension);
		}
		Class<?> arrayClass = componentClass;
		for (int index = 1; index <= dimension; index++) {
			arrayClass = Array.newInstance(arrayClass, 0).getClass();
		}
		return arrayClass;
	}

}
