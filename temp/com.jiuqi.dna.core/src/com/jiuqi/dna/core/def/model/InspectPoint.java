package com.jiuqi.dna.core.def.model;

/**
 * �����㶨�壬�����˶Զ�����Լ���Ĵ���
 * 
 * @author gaojingxin
 * 
 */
public interface InspectPoint {
	/**
	 * ���Է��ض�Ӧ�Ķ�������
	 * 
	 * @return ���Է��ض�Ӧ�Ķ����������null
	 */
	public ModelActionDefine asAction();

	/**
	 * ���Է��ض�Ӧ��Լ������
	 * 
	 * @return ���Է��ض�Ӧ��Լ���������null;
	 */
	public ModelConstraintDefine asConstraint();
}
