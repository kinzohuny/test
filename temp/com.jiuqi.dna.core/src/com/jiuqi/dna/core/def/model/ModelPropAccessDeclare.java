package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.model.ModelService;

/**
 * ģ�����Է���������
 * 
 * @author gaojingxin
 * 
 */
public interface ModelPropAccessDeclare extends ModelPropAccessDefine {
	/**
	 * ��÷������Ľű�
	 * 
	 * @return ���ؽű�����
	 */
	public ScriptDeclare getScript();

	/**
	 * ���Է�������Ӧ���ֶζ���
	 */
	public void setRefField(ModelFieldDefine field);

	/**
	 * ����ģ�����Է�������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵ����Է�����
	 */
	public ModelService<?>.ModelPropertyAccessor setAccessor(
			ModelService<?>.ModelPropertyAccessor accessor);
}
