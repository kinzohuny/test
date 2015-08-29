package com.jiuqi.dna.core.impl;


/**
 * 表定义节点
 * 
 * @author niuhaifeng
 * 
 */
class NTableDeclare extends NAbstractTableDeclare {
	public NTableDeclare(Token start, Token end, TString name,
			NAbstractTableDeclare base, NTablePrimary primary,
			NTableExtend[] extend, NTableIndex[] index,
			NTableRelation[] relation, NTableHierarchy[] hierarchy,
			NTablePartition partition) {
		super(start, end, name, base, primary, extend, index, relation,
				hierarchy, partition);
	}

	@Override
	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitTableDeclare(visitorContext, this);
	}
}
