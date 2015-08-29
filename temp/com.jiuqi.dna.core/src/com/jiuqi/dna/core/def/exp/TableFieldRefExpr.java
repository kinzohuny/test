package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.def.table.TableReferenceDefine;

/**
 * 表字段引用表达式
 * 
 * @author houchunlei
 * 
 */
public interface TableFieldRefExpr extends RelationColumnRefExpr {

	/**
	 * 获取字段定义
	 */
	public TableFieldDefine getColumn();

	/**
	 * 获取所在的表引用定义
	 */
	public TableReferenceDefine getReference();
}