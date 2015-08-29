package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.type.DataObjectTranslator;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;

public abstract class NUnserializer {

	public NUnserializer(ObjectTypeQuerier objectTypeQuerier) {
		if (objectTypeQuerier == null) {
			throw new NullArgumentException("TypeQuerier");
		}
		this.objectTypeQuerier = objectTypeQuerier;
	}

	/**
	 * 反序列化器工厂
	 */
	public static abstract class NUnserializerFactory {
		/**
		 * 获得版本号
		 */
		public final short version;

		/**
		 * 创建反序列化器
		 */
		public abstract NUnserializer newNUnserializer(
				ObjectTypeQuerier objectTypeQuerier);

		NUnserializerFactory(short version) {
			this.version = version;
		}
	}

	/**
	 * 必须按照版本号从大到小的顺序排放
	 */
	private static NUnserializerFactory[] factorys = new NUnserializerFactory[] { NUnserializer_1_1.FACTORY, NUnserializer_1_0.FACTORY };

	/**
	 * 按要求的返回序列化器工厂
	 */
	public static NUnserializerFactory findUnserializerFactory(
			short requiredVersion) {
		for (NUnserializerFactory factory : factorys) {
			if (factory.version == requiredVersion) {
				return factory;
			}
		}
		return null;
	}

	public static NUnserializer newUnserializer(short requiredVersion,
			ObjectTypeQuerier objectTypeQuerier) {
		for (NUnserializerFactory factory : factorys) {
			if (factory.version == requiredVersion) {
				return factory.newNUnserializer(objectTypeQuerier);
			}
		}
		throw new UnsupportedOperationException("不支持版本为" + Integer.toHexString(requiredVersion) + "的反序列化过程");
	}

	/**
	 * 获得序列化器工厂最大版本号
	 */
	public static short getHighestSerializeVersion() {
		return factorys[0].version;
	}

	/**
	 * 获得序列化器版本号
	 */
	public abstract short getVersion();

	/**
	 * 判断当前反序列化过程是否已经完成
	 * 
	 * @return 如果当前反序列化过程已经完成，返回true，否则返回false
	 */
	public abstract boolean isUnserialized();

	/**
	 * 重置反序列化器所有状态
	 */
	public abstract void reset();

	/**
	 * 开始从指定的fragment中反序列化一个对象
	 * 
	 * @param fragment
	 *            指定的fragment，不能为空
	 * @return 如果指定的fragment中提供的字节流不够反序列化一个对象，需要更多的字节完成本次反序列化时，返回false，输入新的字节以后
	 *         ，调用unserializeRest(fragment)方法继续完成反序列化；否则说明反序列化已经完成 ，返回true
	 */
	public abstract boolean unserializeStart(final DataInputFragment fragment,
			Object destHint);

	/**
	 * 把从指定的fragment中反序列化当前反序列化对象未反序列化部分
	 * 
	 * @param fragment
	 *            指定的fragment，不能为空
	 * @return 如果指定的fragment中提供的字节流不够反序列化一个对象，需要更多的字节完成本次反序列化时，返回false，输入新的字节以后
	 *         ，再调用该方法继续完成反序列化；否则说明反序列化已经完成 ，返回true
	 */
	public abstract boolean unserializeRest(final DataInputFragment fragment);

	/**
	 * 获取当前序列化后得到的对象，调用该方法的前提是保证反序列化已完成了本次反序列化过程
	 */
	public abstract Object getUnserialzedObject();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract boolean readBoolean();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract byte readByte();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract short readShort();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract char readChar();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract int readInt();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract float readFloat();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract long readLong();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract double readDouble();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract Object readGUIDField();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract Object readStringField();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract long readDateField();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract Object readEnumField(final DataType declaredType);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract Object readByteArrayField();

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract Object readObject(final DataType declaredType, final Object hint);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract void readObjectArrayElement(final Object hint,
			DataTranslatorHelper<?, ?> dth);

	/**
	 * 该方法仅供内部调用和回调使用
	 */
	abstract void readCustomObject(final Object hint);

	static final Object UNSERIALIZABLE_OBJECT = new Object();

	/**
	 * 对象类型获取器
	 * 
	 * @author gaojingxin
	 * 
	 */
	public interface ObjectTypeQuerier {
		/**
		 * 返回null表示需要挂起并重入
		 * 
		 * @param typeID
		 * @return 返回null表示需要挂起并重入
		 */
		public DataType findElseAsync(GUID typeID);

		public static final ObjectTypeQuerier staticObjectTypeQuerier = new ObjectTypeQuerier() {

			public final DataType findElseAsync(GUID typeID) {
				final DataType dt = DataTypeBase.findDataType(typeID);
				if (dt == null) {
					throw new MissingObjectException("找不到ID为[" + typeID + "]的静态类型");
				}
				return dt;
			}
		};
	}

	private final ObjectTypeQuerier objectTypeQuerier;

	protected DataType tryGetDataType(GUID typeID) {
		return this.objectTypeQuerier.findElseAsync(typeID);
	}

	static final RuntimeException unserializeException() {
		return new RuntimeException("反序列化异常");
	}

	static final class SerializeDataTranslatorHelper<TSourceObject, TDelegateObject>
			extends DataTranslatorHelper<TSourceObject, TDelegateObject> {

		SerializeDataTranslatorHelper(
				DataObjectTranslator<TSourceObject, TDelegateObject> translator,
				ArrayList<Object> objIndex, short version,
				final boolean haveHint) {
			super(translator);
			this.objIndex = objIndex;
			this.index = objIndex.size();
			objIndex.add(this);
			this.version = version;
		}

		private ArrayList<Object> objIndex;
		private final int index;
		private final short version;

		@Override
		protected void destInstanceResolved(TSourceObject dest) {
			objIndex.set(index, dest);
			objIndex = null;
		}

		@Override
		protected short version() {
			return version;
		}

		@Override
		protected boolean forSerial() {
			return true;
		}
	}

}
