package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.model.ModelService;

/**
 * ģ�͹���������ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ModelConstructorDeclare extends ModelConstructorDefine,
		ModelInvokeDeclare {
	/**
	 * �������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ����ģ�͹�������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵĹ�����
	 */
	public ModelService<?>.ModelConstructor<?> setConstructor(
			ModelService<?>.ModelConstructor<?> constructor);

}
