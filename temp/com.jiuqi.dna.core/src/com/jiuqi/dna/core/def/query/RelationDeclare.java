package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * 关系的元定义.描述二维表形式的数据结构的元数据定义.
 * 
 * @see com.jiuqi.dna.core.def.query.RelationDefine
 * 
 * @author houchunlei
 * 
 */
public interface RelationDeclare extends RelationDefine, NamedDeclare {

	public RelationColumnDeclare findColumn(String columnName);

	public RelationColumnDeclare getColumn(String columnName);
}
