package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableReferenceDeclare;

/**
 * 可以构建非连接的关系引用
 * 
 * @author houchunlei
 * 
 */
public interface RelationRefBuildable {

	/**
	 * 构造表引用
	 * 
	 * @param table
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDefine table);

	/**
	 * 构造表引用
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDefine table, String name);

	/**
	 * 构造表引用
	 * 
	 * @param table
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDeclarator table);

	/**
	 * 构造表引用
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDeclarator table, String name);

	/**
	 * 构造查询引用
	 * 
	 * @param query
	 * @return
	 */
	public QueryReferenceDeclare newReference(DerivedQueryDefine query);

	/**
	 * 构造查询引用
	 * 
	 * @param query
	 * @param name
	 * @return
	 */
	public QueryReferenceDeclare newReference(DerivedQueryDefine query,
			String name);
}
