package com.jiuqi.dna.core.impl;

/**
 * �Ӳ�ѯ��Ӧ�Ĳ�ѯ�����
 * 
 * @author houchunlei
 * 
 */
final class SubQueryColumnImpl extends
		SelectColumnImpl<SubQueryImpl, SubQueryColumnImpl> {

	SubQueryColumnImpl(SubQueryImpl owner, String name, String alias,
			ValueExpr expr) {
		super(owner, name, alias, expr);
	}
}