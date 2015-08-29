package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.DataType;

public interface FieldValueAssignable {

	/**
	 * �����ֶεĳ���ֵ
	 * 
	 * @param field
	 *            �ֶζ���
	 * @param value
	 *            ����ֵ
	 */
	public void assignConst(TableFieldDefine field, Object value);

	/**
	 * �����ֶεĲ���ֵ
	 * 
	 * @param field
	 * @param argument
	 */
	public void assignArgument(TableFieldDefine field, ArgumentDefine argument);

	/**
	 * ��ָ�������������͹����������,�������ֶε�ֵΪ�ò���,���ز�������
	 * 
	 * @param field
	 *            ����ı��ֶ�
	 * @param name
	 *            ������
	 * @param type
	 *            ��������
	 * @return
	 */
	public ArgumentDefine assignArgument(TableFieldDefine field, String name,
			DataType type);

	/**
	 * ���ݱ��ֶι����������,�������ֶε�ֵΪ�ò���,���ز�������
	 * 
	 * @param field
	 *            ����ı��ֶ�
	 * @return
	 */
	public ArgumentDefine assignArgument(TableFieldDefine field);

	/**
	 * �����ֶεı��ʽֵ
	 * 
	 * @param field
	 * @param value
	 */
	public void assignExpression(TableFieldDefine field, ValueExpression value);
}
