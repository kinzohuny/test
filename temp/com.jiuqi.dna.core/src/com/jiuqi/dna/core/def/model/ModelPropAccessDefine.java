package com.jiuqi.dna.core.def.model;

/**
 * ģ�����Է��������壬��ģ�����ԵĶ�ȡ���Լ�����������
 * 
 * @author gaojingxin
 * 
 */
public interface ModelPropAccessDefine {
	/**
	 * ��ȡ���������Զ���
	 */
	public ModelPropertyDefine getPropertyDefine();

	/**
	 * ��÷������Ľű�
	 * 
	 * @return ���ؽű�����
	 */
	public ScriptDefine getScript();

	/**
	 * ���Է�������Ӧ���ֶζ���
	 * 
	 * @return �����ֶζ���
	 */
	public ModelFieldDefine getRefField();
}
