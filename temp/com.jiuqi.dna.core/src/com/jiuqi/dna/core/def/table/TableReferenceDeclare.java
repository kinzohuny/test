package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.query.RelationRefDeclare;

/**
 * 表引用接口
 * 
 * @see com.jiuqi.dna.core.def.table.TableReferenceDefine
 * 
 * @author gaojingxin
 */
public interface TableReferenceDeclare extends TableReferenceDefine,
		RelationRefDeclare {

	public TableDeclare getTarget();

	public TableFieldRefExpr expOf(RelationColumnDefine column);

	public TableFieldRefExpr expOf(String columnName);
}