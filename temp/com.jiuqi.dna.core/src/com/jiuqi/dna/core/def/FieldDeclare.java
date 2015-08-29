package com.jiuqi.dna.core.def;

import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * �����õ��ֶλ��ӿڶ���
 * 
 * @author gaojingxin
 * 
 */
public interface FieldDeclare extends FieldDefine, NamedDeclare {
	/**
	 * �����Ƿ�Ҫ�󱣳���Ч
	 * 
	 * @param value �Ƿ�Ҫ�󱣳���Ч
	 */
	public void setKeepValid(boolean value);

	/**
	 * ����ֻ������
	 * 
	 * @param value �Ƿ�ֻ��
	 */
	public void setReadonly(boolean value);

	/**
	 * ����Ĭ��ֵ
	 * 
	 * @param exp Ĭ��ֵ���ʽ
	 */
	public void setDefault(ValueExpression exp);
}
