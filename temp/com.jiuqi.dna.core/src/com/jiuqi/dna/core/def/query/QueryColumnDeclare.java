package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.obja.StructFieldDefine;

/**
 * ��ѯ��䶨�������ж���
 * 
 * @see com.jiuqi.dna.core.def.query.QueryColumnDefine
 * 
 * @author gaojingxin
 * 
 */
public interface QueryColumnDeclare extends QueryColumnDefine,
		SelectColumnDeclare {

	public QueryStatementDeclare getOwner();

	/**
	 * �����ж���ı��ʽ
	 */
	public void setExpression(ValueExpression value);

	/**
	 * ����ӳ�䵽��ģ�͵��ֶ�
	 * 
	 * @param field
	 *            javaʵ�����ԵĽṹ�ֶζ���
	 */
	public void setMapingField(StructFieldDefine field);

	/**
	 * ����ӳ�䵽��ģ�͵��ֶ�
	 * 
	 * @param structFieldName
	 *            javaʵ�����Ե�����(���ִ�Сд)
	 */
	public void setMapingField(String structFieldName);

	/**
	 * �����Ƿ�ʹ�ø߾��ȵ�BigDecimal���Ͷ�ȡ���
	 * 
	 * @param usingBigDecimal
	 */
	public void setUsingBigDecimal(boolean usingBigDecimal);
}