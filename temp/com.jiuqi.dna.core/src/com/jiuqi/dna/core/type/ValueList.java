package com.jiuqi.dna.core.type;
/**
 * ֵ�б�ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ValueList extends Values {
	/**
	 * ��ȡ�������ӿ�
	 * 
	 * @return ���ص������ӿ�
	 */
	public ValueIterator newIterator();
	/**
	 * ����б�ĸ���
	 * 
	 * @return �����б����
	 */
	public int getCount();
}
