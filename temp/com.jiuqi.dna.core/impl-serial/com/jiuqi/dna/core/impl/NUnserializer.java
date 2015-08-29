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
	 * �����л�������
	 */
	public static abstract class NUnserializerFactory {
		/**
		 * ��ð汾��
		 */
		public final short version;

		/**
		 * ���������л���
		 */
		public abstract NUnserializer newNUnserializer(
				ObjectTypeQuerier objectTypeQuerier);

		NUnserializerFactory(short version) {
			this.version = version;
		}
	}

	/**
	 * ���밴�հ汾�ŴӴ�С��˳���ŷ�
	 */
	private static NUnserializerFactory[] factorys = new NUnserializerFactory[] { NUnserializer_1_1.FACTORY, NUnserializer_1_0.FACTORY };

	/**
	 * ��Ҫ��ķ������л�������
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
		throw new UnsupportedOperationException("��֧�ְ汾Ϊ" + Integer.toHexString(requiredVersion) + "�ķ����л�����");
	}

	/**
	 * ������л����������汾��
	 */
	public static short getHighestSerializeVersion() {
		return factorys[0].version;
	}

	/**
	 * ������л����汾��
	 */
	public abstract short getVersion();

	/**
	 * �жϵ�ǰ�����л������Ƿ��Ѿ����
	 * 
	 * @return �����ǰ�����л������Ѿ���ɣ�����true�����򷵻�false
	 */
	public abstract boolean isUnserialized();

	/**
	 * ���÷����л�������״̬
	 */
	public abstract void reset();

	/**
	 * ��ʼ��ָ����fragment�з����л�һ������
	 * 
	 * @param fragment
	 *            ָ����fragment������Ϊ��
	 * @return ���ָ����fragment���ṩ���ֽ������������л�һ��������Ҫ������ֽ���ɱ��η����л�ʱ������false�������µ��ֽ��Ժ�
	 *         ������unserializeRest(fragment)����������ɷ����л�������˵�������л��Ѿ���� ������true
	 */
	public abstract boolean unserializeStart(final DataInputFragment fragment,
			Object destHint);

	/**
	 * �Ѵ�ָ����fragment�з����л���ǰ�����л�����δ�����л�����
	 * 
	 * @param fragment
	 *            ָ����fragment������Ϊ��
	 * @return ���ָ����fragment���ṩ���ֽ������������л�һ��������Ҫ������ֽ���ɱ��η����л�ʱ������false�������µ��ֽ��Ժ�
	 *         ���ٵ��ø÷���������ɷ����л�������˵�������л��Ѿ���� ������true
	 */
	public abstract boolean unserializeRest(final DataInputFragment fragment);

	/**
	 * ��ȡ��ǰ���л���õ��Ķ��󣬵��ø÷�����ǰ���Ǳ�֤�����л�������˱��η����л�����
	 */
	public abstract Object getUnserialzedObject();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract boolean readBoolean();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract byte readByte();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract short readShort();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract char readChar();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract int readInt();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract float readFloat();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract long readLong();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract double readDouble();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract Object readGUIDField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract Object readStringField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract long readDateField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract Object readEnumField(final DataType declaredType);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract Object readByteArrayField();

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract Object readObject(final DataType declaredType, final Object hint);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract void readObjectArrayElement(final Object hint,
			DataTranslatorHelper<?, ?> dth);

	/**
	 * �÷��������ڲ����úͻص�ʹ��
	 */
	abstract void readCustomObject(final Object hint);

	static final Object UNSERIALIZABLE_OBJECT = new Object();

	/**
	 * �������ͻ�ȡ��
	 * 
	 * @author gaojingxin
	 * 
	 */
	public interface ObjectTypeQuerier {
		/**
		 * ����null��ʾ��Ҫ��������
		 * 
		 * @param typeID
		 * @return ����null��ʾ��Ҫ��������
		 */
		public DataType findElseAsync(GUID typeID);

		public static final ObjectTypeQuerier staticObjectTypeQuerier = new ObjectTypeQuerier() {

			public final DataType findElseAsync(GUID typeID) {
				final DataType dt = DataTypeBase.findDataType(typeID);
				if (dt == null) {
					throw new MissingObjectException("�Ҳ���IDΪ[" + typeID + "]�ľ�̬����");
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
		return new RuntimeException("�����л��쳣");
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
