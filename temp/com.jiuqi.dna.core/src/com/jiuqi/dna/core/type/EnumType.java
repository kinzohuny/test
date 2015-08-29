package com.jiuqi.dna.core.type;

/**
 * ö������
 * 
 * @author gaojingxin
 * 
 * @param <TEnum>
 */
public interface EnumType<TEnum extends Enum<TEnum>> extends ObjectDataType {

	/**
	 * ö�ٵ�����
	 * 
	 * @return
	 */
	public Class<TEnum> getEnumClass();

	/**
	 * ö�ٵĸ���
	 */
	public int getCount();

	/**
	 * ��ȡö�ٵ�ֵ
	 * 
	 * @param i
	 * @return
	 */
	public TEnum getEnum(int ordinal);

	/**
	 * ��ȡö�ٵ�ֵ
	 * 
	 * @param name
	 * @return
	 */
	public TEnum getEnum(String name);
}
