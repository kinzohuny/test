package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * ��ѯ���������
 * 
 * @see com.jiuqi.dna.core.def.query.GroupByItemDefine
 * 
 * @author houchunlei
 * 
 */
public interface GroupByItemDeclare extends GroupByItemDefine {

	/**
	 * ���÷������ı��ʽ
	 * 
	 * @param expression
	 */
	public void setExpression(ValueExpression expression);
}
