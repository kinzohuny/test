package com.jiuqi.dna.core.def.obja;

import com.jiuqi.dna.core.def.exp.AssignableExpression;

/**
 * �������ñ��ʽ
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public interface StructFieldRefExpression extends AssignableExpression {

	/**
	 * ������õĲ�����ṹ�Ӷζ���
	 * 
	 * @return ���ز�����ṹ�Ӷζ���
	 */
	public StructFieldDefine getField();
}