package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.FieldDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * 逻辑字段定义
 * 
 * @author gaojingxin
 * 
 */
public interface TableFieldDeclare extends TableFieldDefine, FieldDeclare,
		RelationColumnDeclare {

	/**
	 * 表定义
	 */
	public TableDeclare getOwner();

	/**
	 * 返回所在物理表定义
	 */
	public DBTableDeclare getDBTable();

	/**
	 * 设置是否是逻辑主键
	 */
	public void setPrimaryKey(boolean value);

	/**
	 * 设置默认值
	 * 
	 * @param value
	 *            常量，必须与字段的数据类型相符
	 */
	public void setDefault(Object value);

	/**
	 * 尝试改变字段的长度或精度，只针对非LOB的字符串和二进制串，及定点小数类型有效。
	 * 
	 * @param newType
	 * @return 返回是否调整当前字段的数据类型成为指定的数据类型
	 */
	public boolean adjustType(DataType newType);

	/**
	 * 设置数据库中列名称
	 * 
	 * <p>
	 * 已经废弃的方法，调用该方法没有任何效果。
	 */
	@Deprecated
	public void setNameInDB(String nameInDB);

	/**
	 * 设置是否作为模板字段
	 * 
	 * @param templated
	 */
	public void setTemplated(boolean templated);
}