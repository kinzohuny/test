package com.jiuqi.dna.core.type;


/**
 * ��дֵ�ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface WritableValue extends DataTypable {
	/**
	 * ��Ϊ��ֵ
	 */
	public void setNull();

	/**
	 * ��ֵ
	 */
	public void setObject(Object value);

	/**
	 * ��ֵ
	 */
	public void setValue(ReadableValue value);

	/**
	 * ������
	 */
	public void setBoolean(boolean value);

	/**
	 * �ַ���
	 */
	public void setChar(char value);

	/**
	 * ������
	 */
	public void setShort(short value);

	/**
	 * ����ֵ
	 */
	public void setInt(int value);

	/**
	 * ������
	 */
	public void setLong(long value);

	/**
	 * ����ʱ��
	 */
	public void setDate(long value);

	/**
	 * �����ȸ���С��
	 */
	public void setFloat(float value);

	/**
	 * ˫���ȸ���С��
	 */
	public void setDouble(double value);

	/**
	 * �ַ���
	 */
	public void setString(String value);

	/**
	 * ������ֵ
	 */
	public void setByte(byte value);

	/**
	 * ����������
	 */
	public void setBytes(byte[] value);

	/**
	 * GUID����
	 */
	public void setGUID(GUID guid);
}
