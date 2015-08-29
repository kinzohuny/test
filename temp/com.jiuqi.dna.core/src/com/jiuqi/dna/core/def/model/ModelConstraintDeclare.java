package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.info.InfoDeclare;
import com.jiuqi.dna.core.model.ModelService;

/**
 * ģ��Լ������
 * 
 * @author gaojingxin
 * 
 */
public interface ModelConstraintDeclare extends ModelConstraintDefine,
        InfoDeclare {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDeclare getOwner();

	/**
	 * ������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ����ģ��Լ���������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵ�Լ�������
	 */
	public ModelService<?>.ModelConstraintChecker setChecker(
	        ModelService<?>.ModelConstraintChecker checker);

}
