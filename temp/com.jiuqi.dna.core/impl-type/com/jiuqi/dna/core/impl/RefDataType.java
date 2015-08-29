package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 引用赋值器
 * 
 * @author gaojingxin
 * 
 */
public class RefDataType extends ObjectDataTypeBase {

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(this.javaClass.getName());
	}

	public static final RefDataType objectRefType = new RefDataType(Object.class) {
	};

	public static final RefDataType dateRefType = new RefDataType(java.util.Date.class) {
		@Override
		public final Object convert(Object from) {
			if (from == null) {
				return from;
			}
			final Class<?> fc = from.getClass();
			if (fc == java.util.Date.class) {
				return from;
			}
			if (fc == java.sql.Date.class) {
				return new java.util.Date(((java.sql.Date) from).getTime());
			}
			if (fc == Long.class) {
				return new java.util.Date((Long) from);
			}
			return super.convert(from);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == dateRefType || another == sqlDateRefType || another == DateType.TYPE) {
				return AssignCapability.SAME;
			} else if (another == LongType.TYPE) {
				return AssignCapability.IMPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType sqlDateRefType = new RefDataType(java.sql.Date.class) {
		@Override
		public final Object convert(Object from) {
			if (from == null) {
				return from;
			}
			final Class<?> fc = from.getClass();
			if (fc == java.sql.Date.class) {
				return from;
			}
			if (fc == java.util.Date.class) {
				return new java.sql.Date(((java.util.Date) from).getTime());
			}
			if (fc == Long.class) {
				return new java.sql.Date((Long) from);
			}
			return super.convert(from);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == dateRefType || another == sqlDateRefType || another == DateType.TYPE) {
				return AssignCapability.SAME;
			} else if (another == LongType.TYPE) {
				return AssignCapability.IMPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType enumBaseDataType = new RefDataType(java.lang.Enum.class) {
	};

	static abstract class BoxingDataType extends RefDataType {

		BoxingDataType(Class<?> javaClass) {
			super(javaClass);
			this.forOldSerial = new StaticStructDefineImpl(javaClass);
		}

		final StaticStructDefineImpl forOldSerial;

		@Override
		ObjectDataTypeInternal tryGetOldSerialDataType() {
			return this.forOldSerial;
		}

		@Override
		public Object convert(Object from) {
			if (from == null || this.javaClass.isInstance(from)) {
				return from;
			}
			return this.cast(from);
		}

		abstract Object cast(Object value);
	}

	public static final RefDataType booleanObjType = new BoxingDataType(Boolean.class) {

		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeBooleanObject((Boolean) obj);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toBoolean(value);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == this) {
				return AssignCapability.SAME;
			} else if (another instanceof BooleanType) {
				return AssignCapability.IMPLICIT;
			} else if (another instanceof NumberType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType byteObjType = new BoxingDataType(Byte.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object object) {
			return serializer.writeByteObject((Byte) object);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toByte(value);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == this) {
				return AssignCapability.SAME;
			} else if (another instanceof NumberType) {
				return AssignCapability.IMPLICIT;
			} else if (another == shortObjType || another == intObjType || another == longObjType || another == floatObjType || another == doubleObjType || another == bigIntegerType || another == byteObjType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType shortObjType = new BoxingDataType(Short.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeShortObject((Short) obj);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toShort(value);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == this) {
				return AssignCapability.SAME;
			} else if (another instanceof NumberType || another == shortObjType) {
				return AssignCapability.IMPLICIT;
			} else if (another == intObjType || another == longObjType || another == floatObjType || another == doubleObjType || another == bigIntegerType || another == byteObjType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType charObjType = new BoxingDataType(Character.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeCharObject((Character) obj);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toChar(value);
		}
	};

	public static final RefDataType intObjType = new BoxingDataType(Integer.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeIntObject((Integer) obj);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toInt(value);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == this) {
				return AssignCapability.SAME;
			} else if (another instanceof NumberType || another == shortObjType || another == intObjType) {
				return AssignCapability.IMPLICIT;
			} else if (another == longObjType || another == floatObjType || another == doubleObjType || another == bigIntegerType || another == byteObjType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType longObjType = new BoxingDataType(Long.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeLongObject((Long) obj);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toLong(value);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == this) {
				return AssignCapability.SAME;
			} else if (another instanceof NumberType || another == shortObjType || another == intObjType || another == longObjType) {
				return AssignCapability.IMPLICIT;
			} else if (another == floatObjType || another == doubleObjType || another == bigIntegerType || another == byteObjType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType floatObjType = new BoxingDataType(Float.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeFloatObject((Float) obj);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toFloat(value);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == this) {
				return AssignCapability.SAME;
			} else if (another instanceof NumberType || another == shortObjType || another == intObjType || another == longObjType || another == floatObjType) {
				return AssignCapability.IMPLICIT;
			} else if (another == doubleObjType || another == bigIntegerType || another == byteObjType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType doubleObjType = new BoxingDataType(Double.class) {
		@Override
		public final boolean nioSerializeData(final NSerializer serializer,
				final Object obj) {
			return serializer.writeDoubleObject((Double) obj);
		}

		@Override
		public final Object cast(Object value) {
			return Convert.toDouble(value);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == this) {
				return AssignCapability.SAME;
			} else if (another instanceof NumberType || another == shortObjType || another == intObjType || another == longObjType || another == floatObjType || another == doubleObjType) {
				return AssignCapability.IMPLICIT;
			} else if (another == bigIntegerType || another == byteObjType) {
				return AssignCapability.EXPLICIT;
			}
			return AssignCapability.NO;
		}
	};

	public static final RefDataType bigIntegerType = new RefDataType(BigInteger.class) {

		@Override
		public Object convert(Object from) {
			return Convert.toBigInteger(from);
		}
	};

	public static final RefDataType bigDecimalType = new RefDataType(BigDecimal.class) {

		@Override
		public Object convert(Object from) {
			return Convert.toBigDecimal(from);
		}

		@Override
		public AssignCapability isAssignableFrom(DataType another) {
			if (another == bigDecimalType) {
				return AssignCapability.SAME;
			} else if (another instanceof NumberType || another == bigIntegerType || another == byteObjType || another == shortObjType || another == intObjType || another == longObjType || another == floatObjType || another == doubleObjType) {
				return AssignCapability.IMPLICIT;
			}
			return AssignCapability.NO;
		}
	};
	/**
	 * 拒绝结构对象
	 */
	boolean rejectStruct;

	static {
		// 资源项
		new RefDataType(ZEROReadableValue.INSTANCE);
		new RefDataType(NULLReadableValue.INSTANCE);
		new RefDataType(UNKNOWNReadableValue.INSTANCE);
	}

	/**
	 * 确保该类静态数据被JVM初始
	 */
	@SuppressWarnings("deprecation")
	static void ensureStaticInited() {
		// 各列表类型
		ListDataType.ensureStaticInited();
	}

	RefDataType(Class<?> javaClass, boolean rejectStruct) {
		super(javaClass);
		this.rejectStruct = rejectStruct;
	}

	RefDataType(Class<?> javaClass) {
		this(javaClass, false);
	}

	<TClass> RefDataType(TClass singleton) {
		this(singleton.getClass(), true);
		this.registerDataObjectTranslator(new DOT_SingletonTranslator<TClass>(singleton));
	}

	@Override
	public AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		if (another instanceof ObjectDataType) {
			Class<?> anotherJavaClass = ((ObjectDataType) another).getJavaClass();
			if (anotherJavaClass == this.javaClass) {
				return AssignCapability.SAME;
			}
			if (this.javaClass.isAssignableFrom(anotherJavaClass)) {
				return AssignCapability.IMPLICIT;
			}
			if (anotherJavaClass.isAssignableFrom(this.javaClass)) {
				return AssignCapability.EXPLICIT;
			}
		}
		return AssignCapability.NO;
	}

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.OBJECT);
		digester.update(this.javaClass);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.OBJECT) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				return DataTypeBase.dataTypeOfJavaClass(undigester.extractClass());
			}
		});
	}

	// // /////////////////////////////////////////
	// // Serialization
	/**
	 * 为兼容旧的序列化功能而建立的
	 * 
	 * @return
	 */
	@Deprecated
	ObjectDataTypeInternal tryGetOldSerialDataType() {
		return this;
	}

	@Override
	public boolean supportSerialization() {
		return false;
	}

	@Override
	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException(obj.getClass().getName() + "类型的对象不支持D＆A-Core框架定义的序列化");
	}

	@Override
	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException();

	}
}
