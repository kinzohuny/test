package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * ģ�͹�ϵ����
 * 
 * @author gaojingxin
 * 
 */
public interface ModelReferenceDeclare extends ModelReferenceDefine,
		NamedDeclare {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDeclare getOwner();
}
