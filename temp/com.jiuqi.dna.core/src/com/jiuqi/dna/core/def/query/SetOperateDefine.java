package com.jiuqi.dna.core.def.query;

/**
 * ��������
 * 
 * @author houchunlei
 * 
 */
public interface SetOperateDefine {

	/**
	 * ��ȡ���������
	 * 
	 * @return
	 */
	public SetOperator getOperator();

	/**
	 * ��ȡ�����������
	 * 
	 * @return
	 */
	public DerivedQueryDefine getTarget();
}
