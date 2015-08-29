package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.def.query.RelationDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * 逻辑表定义
 * 
 * @see com.jiuqi.dna.core.def.table.TableDefine
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings("deprecation")
public interface TableDeclare extends TableDefine, TablePartitionDeclare,
		RelationDeclare {

	public ModifiableNamedElementContainer<? extends DBTableDeclare> getDBTables();

	public DBTableDeclare getPrimaryDBTable();

	/**
	 * 增加物理表定义
	 * 
	 * <p>
	 * 逻辑表最多包含32个物理表
	 * 
	 * @param name
	 *            物理表定义名称，同数据库中创建表名称
	 * @return
	 */
	public DBTableDeclare newDBTable(String name);

	public TableFieldDeclare findColumn(String columnName);

	public TableFieldDeclare getColumn(String columnName);

	public ModifiableNamedElementContainer<? extends TableFieldDeclare> getFields();

	/**
	 * 增加逻辑主键字段定义
	 * 
	 * @param name
	 *            字段定义名称
	 * @param type
	 *            字段定义类型
	 * @return
	 * @see com.jiuqi.dna.core.type.TypeFactory;
	 */
	public TableFieldDeclare newPrimaryField(String name, DataType type);

	/**
	 * 增加字段定义
	 * 
	 * 
	 * @param name
	 *            字段定义名称
	 * @param type
	 *            字段定义类型
	 * @return
	 * @see com.jiuqi.dna.core.type.TypeFactory;
	 */
	public TableFieldDeclare newField(String name, DataType type);

	/**
	 * 增加字段定义，存储在指定的物理表上
	 * 
	 * @param name
	 *            字段定义名称
	 * @param type
	 *            字段定义类型
	 * @param dbTable
	 *            存储物理表，必须属于当前逻辑表
	 * @return
	 * @see com.jiuqi.dna.core.type.TypeFactory;
	 */
	public TableFieldDeclare newField(String name, DataType type,
			DBTableDefine dbTable);

	public ModifiableNamedElementContainer<? extends IndexDeclare> getIndexes();

	/**
	 * 在主物理表上增加索引定义
	 * 
	 * @param name
	 *            索引名称，与数据库真正创建索引的名称一致
	 * @return
	 */
	public IndexDeclare newIndex(String name);

	/**
	 * 在主物理表上增加指定类型的索引定义
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public IndexDeclare newIndex(String name, IndexType type);

	/**
	 * 增加索引定义
	 * 
	 * @param name
	 *            索引名称，与数据库真正创建索引的名称一致
	 * @param field
	 *            索引字段
	 * @return
	 */
	public IndexDeclare newIndex(String name, TableFieldDefine field);

	/**
	 * 增加索引定义
	 * 
	 * @param name
	 *            索引名称，与数据库真正创建索引的名称一致
	 * @param field
	 *            索引字段
	 * @param others
	 *            其他索引字段
	 * @return
	 */
	public IndexDeclare newIndex(String name, TableFieldDefine field,
			TableFieldDefine... others);

	/**
	 * 创建指定类型的索引定义
	 * 
	 * @param name
	 * @param type
	 * @param field
	 * @param others
	 * @return
	 */
	public IndexDeclare newIndex(String name, IndexType type,
			TableFieldDefine field, TableFieldDefine... others);

	public ModifiableNamedElementContainer<? extends TableRelationDeclare> getRelations();

	/**
	 * 增加表关系定义
	 * 
	 * @param name
	 *            表关系名称
	 * @param target
	 *            表关系的目标表
	 * @param type
	 *            表关系类型
	 * @return
	 */
	public TableRelationDeclare newRelation(String name, TableDefine target,
			TableRelationType type);

	/**
	 * 增加表关系定义
	 * 
	 * @param name
	 *            表关系名称
	 * @param target
	 *            表关系的目标表
	 * @param type
	 *            表关系类型
	 * @return
	 */
	public TableRelationDeclare newRelation(String name,
			TableDeclarator target, TableRelationType type);

	/**
	 * 增加等值表关系定义
	 * 
	 * @param name
	 *            表关系名称
	 * @param selfField
	 *            等值条件在本表的字段
	 * @param target
	 *            表关系的目标表
	 * @param targetField
	 *            等值条件在目标表的字段
	 * @param type
	 *            表关系类型
	 * @return
	 */
	public TableRelationDeclare newRelation(String name,
			TableFieldDefine selfField, TableDefine target,
			TableFieldDefine targetField, TableRelationType type);

	/**
	 * 增加等值表关系定义
	 * 
	 * @param name
	 *            表关系名称
	 * @param selfField
	 *            等值条件在本表的字段
	 * @param target
	 *            表关系的目标表
	 * @param targetField
	 *            等值条件在目标表的字段
	 * @param type
	 *            表关系类型
	 * @return
	 */
	public TableRelationDeclare newRelation(String name,
			TableFieldDefine selfField, TableDeclarator target,
			TableFieldDefine targetField, TableRelationType type);

	@Deprecated
	public ModifiableNamedElementContainer<? extends HierarchyDeclare> getHierarchies();

	/**
	 * 新增级次定义
	 * 
	 * <p>
	 * 逻辑表最多包含32个级次定义
	 * 
	 * @param name
	 *            级次定义名称
	 * @param maxlevel
	 *            级次的最大深度
	 */
	@Deprecated
	public HierarchyDeclare newHierarchy(String name, int maxlevel);

	/**
	 * 设置目录
	 * 
	 * @param category
	 */
	public void setCategory(String category);

	/**
	 * 构造字段引用表达式
	 * 
	 * <p>
	 * 该表达式只能在表关系的条件中使用,不能在任何增删改查语句中使用。
	 * 
	 * @param field
	 *            属于当前表的字段定义
	 * @return
	 */
	public TableFieldRefExpr expOf(TableFieldDefine field);
	
	/**
	 * 设置逻辑表发布时候的类型，NORMAL（普通表），GLOBAL_TEMPORARY（全局临时表）
	 * 
	 * @param type void
	 */
	public void setTableType(TableType type);
	
	/**
	 * 获取逻辑表类型
	 * 
	 * @return TableType
	 * 			NORMAL（普通表），GLOBAL_TEMPORARY（全局临时表）
	 */
	public TableType getTableType();
}