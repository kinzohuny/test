package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.info.InfoDefine;

/**
 * ģ��Լ������
 * 
 * @author gaojingxin
 * 
 */
public interface ModelConstraintDefine extends InfoDefine {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDefine getOwner();

	/**
	 * ������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDefine getScript();
}
