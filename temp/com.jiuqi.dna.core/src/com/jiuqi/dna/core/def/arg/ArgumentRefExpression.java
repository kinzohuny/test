package com.jiuqi.dna.core.def.arg;

import com.jiuqi.dna.core.def.exp.AssignableExpression;

/**
 * �������ñ��ʽ
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings("deprecation")
public interface ArgumentRefExpression extends AssignableExpression {

	/**
	 * ������õĲ�����ṹ�Ӷζ���
	 * 
	 * @return ���ز�����ṹ�Ӷζ���
	 */
	public ArgumentDefine getArgument();
}