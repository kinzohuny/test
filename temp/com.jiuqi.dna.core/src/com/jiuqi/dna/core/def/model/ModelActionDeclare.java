package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.model.ModelService;

/**
 * ģ�Ͷ�������ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ModelActionDeclare extends ModelActionDefine,
		ModelInvokeDeclare {
	/**
	 * �����Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ����ģ�Ͷ�����������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵĴ�����
	 */
	public ModelService<?>.ModelActionHandler<?> setHandler(
			ModelService<?>.ModelActionHandler<?> handler);
}
