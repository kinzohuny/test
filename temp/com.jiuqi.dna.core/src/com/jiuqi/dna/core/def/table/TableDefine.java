package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.query.RelationDefine;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.type.TupleType;

/**
 * 逻辑表定义
 * 
 * @author gaojingxin
 */
@SuppressWarnings("deprecation")
public interface TableDefine extends TablePartitionDefine, MetaElement,
		TupleType, RelationDefine {

	/**
	 * DUMMY表,类似于Oracle中的dual表.
	 */
	public static final TableDefine DUMMY = TableDefineImpl.DUMMY;

	/**
	 * 是否是原生表
	 * 
	 * <p>
	 * 原生表表示通过TableDeclarator代码固化的静态逻辑表
	 * 
	 * @return
	 */
	public boolean isOriginal();

	/**
	 * 返回表定义的行标识列的字段定义
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECID();

	/**
	 * 返回表定义的行版本列的字段定义
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECVER();

	/**
	 * 获取物理表定义列表
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends DBTableDefine> getDBTables();

	/**
	 * 获取主物理表定义
	 * 
	 * @return
	 */
	public DBTableDefine getPrimaryDBTable();

	public TableFieldDefine findColumn(String columnName);

	public TableFieldDefine getColumn(String columnName);

	/**
	 * 获取字段定义列表
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends TableFieldDefine> getFields();

	/**
	 * 获得索引定义列表
	 * 
	 * @return 返回索引定义列表
	 */
	public NamedElementContainer<? extends IndexDefine> getIndexes();

	/**
	 * 获得表关系定义列表
	 * 
	 * @return 返回表关系定义列表
	 */
	public NamedElementContainer<? extends TableRelationDefine> getRelations();

	/**
	 * 获得级次定义列表
	 * 
	 * @return 返回级次定义列表
	 */
	@Deprecated
	public NamedElementContainer<? extends HierarchyDefine> getHierarchies();

	/**
	 * 获得表的分类
	 */
	public String getCategory();
	
	/**
	 * 获取逻辑表类型
	 * 
	 * @return TableType
	 * 			NORMAL（普通表），GLOBAL_TEMPORARY（全局临时表）
	 */
	public TableType getTableType();
}