package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.DefineBase;
import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * ��ѯ���������
 * 
 * @author houchunlei
 * 
 */
public interface GroupByItemDefine extends DefineBase {

	/**
	 * ��ȡ����
	 */
	public ValueExpression getExpression();
}
