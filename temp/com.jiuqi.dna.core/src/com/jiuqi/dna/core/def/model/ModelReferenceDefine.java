package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * ģ�͹�ϵ����
 * 
 * @author gaojingxin
 * 
 */
public interface ModelReferenceDefine extends NamedDefine {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDefine getOwner();

	/**
	 * ��ȡĿ��ģ��
	 * 
	 * @return ����Ŀ��ģ��
	 */
	public ModelDefine getTarget();
}
