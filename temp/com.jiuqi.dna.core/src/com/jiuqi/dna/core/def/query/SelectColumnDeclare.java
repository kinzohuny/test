package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * ��ѯ���ѡ���ж���
 * 
 * @author houchunlei
 * 
 */
public interface SelectColumnDeclare extends SelectColumnDefine,
		RelationColumnDeclare {

	public SelectDeclare getOwner();

	/**
	 * �����ж���ı��ʽ
	 */
	public void setExpression(ValueExpression value);
}
