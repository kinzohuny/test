package com.jiuqi.dna.core.internal.db.sync;

/**
 * �����͵Ŀ��޸���
 * 
 * <p>
 * �����ǿ��޸��Ա���,�������б�����,����Լ��,ת�����¾��ȶ�ʧ���龰.
 * 
 * @author houchunlei
 * 
 */
public enum TypeAlterability {

	/**
	 * ����֧�������޸�
	 */
	Always,

	/**
	 * ���м�¼�ĸ���ֵΪ�ղ����޸�����,Ҳ����ζ�ű�Ϊ�����.
	 */
	ColumnNull,

	/**
	 * �³��Ȼ򾫶ȳ��������Ѿ����ڵ�ֵ.���ݲ��ᶪʧ
	 */
	ExceedExist,

	/**
	 * ���κ������¶��������޸�,��ʹ��Ϊ��
	 */
	Never
}
