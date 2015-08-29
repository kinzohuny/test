package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.type.Type;

/**
 * ��ѯ��䶨��
 * 
 * @author gaojingxin
 * 
 */
public interface QueryStatementDefine extends SelectDefine, StatementDefine,
		WithableDefine, Type {

	public NamedElementContainer<? extends QueryColumnDefine> getColumns();

	/**
	 * ��ȡ�����������
	 * 
	 * @return δ�����򷵻�null
	 */
	public Container<? extends OrderByItemDefine> getOrderBys();

}
