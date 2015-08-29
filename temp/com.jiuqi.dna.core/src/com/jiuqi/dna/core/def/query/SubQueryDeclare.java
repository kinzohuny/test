package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.PredicateExpression;

/**
 * �Ӳ�ѯ����
 * 
 * @see com.jiuqi.dna.core.def.query.SubQueryDefine
 * 
 * @author houchunlei
 * 
 */
public interface SubQueryDeclare extends SubQueryDefine, SelectDeclare {

	/**
	 * ����Exitsν�ʱ��ʽ
	 * 
	 * @return
	 */
	public PredicateExpression exists();

	/**
	 * ����Not Existsν�ʱ��ʽ
	 * 
	 * @return
	 */
	public PredicateExpression notExists();

}
