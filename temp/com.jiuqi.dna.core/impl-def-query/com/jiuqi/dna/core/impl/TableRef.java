package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.table.TableReferenceDeclare;

/**
 * 表引用的内部接口
 * 
 * @author houchunlei
 * 
 */
public interface TableRef extends RelationRef, TableReferenceDeclare {

	static final String xml_attr_table = "table";

	TableDefineImpl getTarget();

	TableFieldRefImpl expOf(RelationColumnDefine column);

	TableFieldRefImpl expOf(String relationColumnName);
}