package com.jiuqi.dna.core.type;

import com.jiuqi.dna.core.impl.NULLReadableValue;
import com.jiuqi.dna.core.impl.UNKNOWNReadableValue;
import com.jiuqi.dna.core.impl.ZEROReadableValue;

/**
 * �ɶ�ֵ�ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ReadableValue extends DataTypable {
	/**
	 * ��ֵ������ΪNone.NONE���ַ���Ϊ""��GUIDΪGUID.empty����������Ϊ0
	 */
	public static final ReadableValue ZERO = ZEROReadableValue.INSTANCE;
	/**
	 * ��ֵ������Ϊnull���ַ���Ϊnull��GUIDΪnull����������Ϊ0
	 */
	public static final ReadableValue NULL = NULLReadableValue.INSTANCE;
	/**
	 * ���ֵ��ö����쳣
	 */
	public static final ReadableValue UNKNOWN = UNKNOWNReadableValue.INSTANCE;

	/**
	 * �����Ƿ�Ϊ��
	 * 
	 * @return �����Ƿ�Ϊ��
	 */
	public boolean isNull();

	/**
	 * ��ֵ
	 * 
	 * @return ����ֵ
	 */
	public Object getObject();

	/**
	 * ��ò�����
	 * 
	 * @return ���ز�����
	 */
	public boolean getBoolean();

	/**
	 * �ַ�����
	 * 
	 * @return �����ַ�����
	 */
	public char getChar();

	/**
	 * �ֽ���
	 */
	public byte getByte();

	/**
	 * ������
	 */
	public short getShort();

	/**
	 * �������ֵ
	 * 
	 * @return ��������ֵ
	 */
	public int getInt();

	/**
	 * ������
	 */
	public long getLong();

	/**
	 * �������ʱ��
	 * 
	 * @return �������ڶ�Ӧ�ĳ�����
	 */
	public long getDate();

	/**
	 * �����ȸ�����
	 */
	public float getFloat();

	/**
	 * ���˫������ֵ
	 * 
	 * @return ���ظ���ֵ
	 */
	public double getDouble();

	/**
	 * ��ö�����ֵ
	 * 
	 * @return ��ö�����ֵ
	 */
	public byte[] getBytes();

	/**
	 * ����ַ�����ֵ
	 * 
	 * @return �����ַ���
	 */
	public String getString();

	/**
	 * ���GUID����ֵ
	 */
	public GUID getGUID();
}
