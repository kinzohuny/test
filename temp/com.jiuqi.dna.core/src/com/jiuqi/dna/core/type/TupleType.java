package com.jiuqi.dna.core.type;

/**
 * Ԫ������
 * 
 * @author gaojingxin
 * 
 */
public interface TupleType extends Type {
	/**
	 * ��ȡԪ���Ԫ�ظ���
	 * 
	 * @return ����Ԫ�ظ���
	 */
	public int getTupleElementCount();

	/**
	 * ��ȡĳ��Ԫ�������
	 * 
	 * @param index λ��
	 * @return ����Ԫ�������
	 */
	public Typable getTupleElementType(int index);
}
